package ddwu.mobile.finalproject.ma02_20180994;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class MovieXmlParser {
    private XmlPullParser parser;

    private enum TagType { None, TITLE, IMAGE, DIRECTOR, ACTOR, RATE }

    private final static String FAULT_RESULT = "faultResult";
    private final static String ITEM_TAG = "item";
    private final static String TITLE = "title";
    private final static String IMAGE = "image";
    private final static String DIRECTOR = "director";
    private final static String ACTOR = "actor";
    private final static String RATE = "userRating";

    public MovieXmlParser() {

        try {
            parser = XmlPullParserFactory.newInstance().newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<MovieDTO> parse(String xml) {
        ArrayList<MovieDTO> resultList = new ArrayList();
        MovieDTO nbd = null;
        TagType tagType = TagType.None;

        try {
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if(tag.equals(ITEM_TAG)) {
                            nbd = new MovieDTO();
                        } else if(tag.equals(TITLE)) {
                            if(nbd != null) {
                                tagType = TagType.TITLE;
                            }
                            else {
                                break;
                            }
                        } else if(tag.equals(IMAGE)) {
                            tagType = TagType.IMAGE;
                        } else if(tag.equals(DIRECTOR)) {
                            tagType = TagType.DIRECTOR;
                        } else if(tag.equals(ACTOR)) {
                            if(nbd != null) {
                                tagType = TagType.ACTOR;
                            }
                            else {
                                break;
                            }
                        } else if(tag.equals(RATE)) {
                            tagType = TagType.RATE;
                        } else if(tag.equals(FAULT_RESULT)) {
                            return null;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals(ITEM_TAG)) {
                            resultList.add(nbd);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case TITLE:
                                nbd.setTitle(parser.getText());
                                break;
                            case IMAGE:
                                nbd.setImage(parser.getText());
                                break;
                            case DIRECTOR:
                                nbd.setDirector(parser.getText());
                                break;
                            case ACTOR:
                                nbd.setActor(parser.getText());
                                break;
                            case RATE:
                                nbd.setUserRating(parser.getText());
                                break;
                        }
                        tagType = TagType.None;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return resultList;
    }

}
