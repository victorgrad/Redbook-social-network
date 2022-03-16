package ro.ubb.map.gtsn.guitoysocialnetwork.bussiness;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Friendship;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Message;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Pair;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.User;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReportService {
    private final FriendshipService friendshipService;
    private final MessageService messageService;
    private final UserService userService;

    private final String font = "fonts/VeraMono.ttf";
    private final int fontSize = 12;
    private final float leading = 14.5f;
    private final int maxWidth = 75;
    private final int maxLength = 49;

    public ReportService(FriendshipService friendshipService, MessageService messageService, UserService userService) {
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.userService = userService;
    }

    public void generateActivityReport(String username, String url, LocalDate begin, LocalDate end) throws IOException {
        if(begin.isAfter(end)){
            throw new ValidationException("Begin date must be before end date");
        }

        User user = userService.getUser(username);

        PDDocument document = new PDDocument();
        PDDocumentInformation documentInformation = document.getDocumentInformation();
        documentInformation.setTitle("UserReport");

        PDPage page = new PDPage();
        document.addPage(page);
        int lines = 0;

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(PDType0Font.load(document, new File(font)), fontSize);

        contentStream.setLeading(leading);
        contentStream.beginText();

        contentStream.newLineAtOffset(25, 750);

        contentStream.showText("User " + user.getId() + " : " + user.getFirstName() + " " + user.getLastName());
        contentStream.newLine();
        contentStream.showText("Showing new friends from " + begin.format(Constants.DATE_TIME_FORMATTER) + " to " + end.format(Constants.DATE_TIME_FORMATTER) + ":");
        contentStream.newLine();
        contentStream.newLine();

        contentStream.showText("New friends:");
        contentStream.newLine();

        lines = 4;

        int pageNum = 0;
        boolean ok = true;
        Iterable<User> friends;

        do {
            ok = false;
            friends = friendshipService.getUserFriendsPage(username, pageNum);
            pageNum += 1;

            for(User userFr: friends){
                Friendship friendship = friendshipService.getFriendship(new Pair<>(username, userFr.getId()));
                if(friendship.getFriendshipDate().isAfter(ChronoLocalDate.from(begin)) && friendship.getFriendshipDate().isBefore(ChronoLocalDate.from(end))){
                    contentStream.showText(userFr.getId() + " : " + userFr.getFirstName() + " " + userFr.getLastName());
                    contentStream.showText("  Date: " + friendship.getFriendshipDate().format(Constants.DATE_TIME_FORMATTER));
                    contentStream.newLine();
                    ok = true;

                    lines += 1;
                    if(lines >= maxLength){
                        contentStream.endText();
                        contentStream.close();

                        page = new PDPage();
                        document.addPage(page);

                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(PDType0Font.load(document, new File(font)), fontSize);

                        contentStream.setLeading(leading);
                        contentStream.beginText();

                        contentStream.newLineAtOffset(25, 750);

                        lines = 0;
                    }
                }
            }

        }while (ok);

        contentStream.endText();
        contentStream.close();

        page = new PDPage();
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(PDType0Font.load(document, new File(font)), fontSize);

        contentStream.setLeading(leading);
        contentStream.beginText();

        contentStream.newLineAtOffset(25, 750);
        lines = 0;

        contentStream.showText("Message Report from " + begin.format(Constants.DATE_TIME_FORMATTER) + " to " + end.format(Constants.DATE_TIME_FORMATTER));
        contentStream.newLine();
        contentStream.newLine();

        lines = 2;

        Map<String, Long> report = getMessageReport(username, begin, end);
        for (Map.Entry<String, Long> entry: report.entrySet()){
            User u = userService.getUser(entry.getKey());
            String text = "With ( " + u.getId() + " ) " + u.getFirstName() + " " + u.getLastName() + "  " + entry.getValue() + " messages;";
            contentStream.showText(text);
            contentStream.newLine();
            lines += 1;

            if(lines >= maxLength){
                contentStream.endText();
                contentStream.close();

                page = new PDPage();
                document.addPage(page);

                contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType0Font.load(document, new File(font)), fontSize);

                contentStream.setLeading(leading);
                contentStream.beginText();

                contentStream.newLineAtOffset(25, 750);

                lines = 0;
            }
        }
        contentStream.endText();
        contentStream.close();

        document.save(url);
        document.close();
    }

    private Map<String, Long> getMessageReport(String username, LocalDate begin, LocalDate end){
        if(begin.isAfter(end)){
            throw new ValidationException("Begin date must be before end date");
        }

        Map<String, Long> report = new HashMap<>();

        int page = 0;
        Set<Message> messages;

        do {
            messages = messageService.getUserMessagesPage(username, page);
            page += 1;

            for(Message m: messages) {
                if (m.getMessageDateTime().toLocalDate().isAfter(ChronoLocalDate.from(begin)) && m.getMessageDateTime().toLocalDate().isBefore(ChronoLocalDate.from(end))) {
                    Set<String> users = m.getTo();
                    users.add(m.getFrom());

                    for (String user : users) {
                        if (!user.equals(username)) {
                            if (report.containsKey(user)) {
                                report.put(user, report.get(user) + 1);
                            } else {
                                report.put(user, 1L);
                            }
                        }
                    }
                }
            }
        }while (!messages.isEmpty());

        return report;
    }

    public void generateMessageReport(String username1, String username2, String url, LocalDate beginDate, LocalDate endDate) throws IOException {
        User user1 = userService.getUser(username1);
        User user2 = userService.getUser(username2);

        PDDocument document = new PDDocument();
        PDDocumentInformation documentInformation = document.getDocumentInformation();
        documentInformation.setTitle("UserReport");

        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(PDType0Font.load(document, new File(font)), fontSize);

        contentStream.setLeading(leading);
        contentStream.beginText();

        contentStream.newLineAtOffset(25, 750);
        contentStream.showText("Showing messages between (" + user1.getId() + ") " + user1.getFirstName() + " " + user1.getLastName());
        contentStream.showText(" and (" + user2.getId() + ") " + user2.getFirstName() + " " + user2.getLastName());
        contentStream.newLine();
        contentStream.newLine();

        int lines = 2;

        int pageNum = 0;
        Set<Message> messages;

        do{
            messages = messageService.getConversationPage(username1, username2, pageNum);
            pageNum += 1;

            for(Message m: messages) {
                if(m.getMessageDateTime().toLocalDate().isAfter(ChronoLocalDate.from(beginDate)) && m.getMessageDateTime().toLocalDate().isBefore(ChronoLocalDate.from(endDate))) {

                    if (lines < maxLength - 5) {
                        contentStream.showText("From: " + m.getFrom());
                        contentStream.newLine();
                        lines += 1;
                        if (m.getFrom().equals(username1)) {
                            contentStream.showText("To: " + username2);
                        } else {
                            contentStream.showText("To: " + username1);
                        }
                        if (m.getTo().size() > 1) {
                            contentStream.showText(" + other " + (m.getTo().size() - 1));
                        }
                        contentStream.newLine();
                        lines += 1;
                        contentStream.showText("Date: " + m.getMessageDateTime().format(Constants.DATE_TIME_FORMATTER2));
                        contentStream.newLine();
                        lines += 1;

                        String[] split = m.getMessage().split("\n");

                        for (String str : split) {

                            while (!str.isEmpty()) {
                                int end = maxWidth;
                                if (end > str.length()) {
                                    end = str.length();
                                }
                                String split2 = str.substring(0, end);
                                str = str.substring(end);

                                contentStream.showText(split2);
                                contentStream.newLine();

                                lines += 1;
                                if (lines >= maxLength) {
                                    page = new PDPage();
                                    document.addPage(page);

                                    contentStream.endText();
                                    contentStream.close();

                                    contentStream = new PDPageContentStream(document, page);
                                    contentStream.setFont(PDType0Font.load(document, new File(font)), fontSize);
                                    contentStream.setLeading(leading);
                                    contentStream.beginText();

                                    contentStream.newLineAtOffset(25, 750);
                                    lines = 0;
                                }
                            }
                        }
                    }
                    else{
                        page = new PDPage();
                        document.addPage(page);

                        contentStream.endText();
                        contentStream.close();

                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(PDType0Font.load(document, new File(font)), fontSize);
                        contentStream.setLeading(leading);
                        contentStream.beginText();

                        contentStream.newLineAtOffset(25, 750);
                        lines = 0;
                    }

                    contentStream.newLine(); lines += 1;
                    contentStream.newLine(); lines += 1;
                }
            }

        }while (!messages.isEmpty());

        contentStream.endText();
        contentStream.close();

        document.save(url);
        document.close();
    }
}
