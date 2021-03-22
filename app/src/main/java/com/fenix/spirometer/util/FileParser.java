package com.fenix.spirometer.util;

import com.fenix.spirometer.model.BaseModel;
import com.fenix.spirometer.model.City;
import com.fenix.spirometer.model.County;
import com.fenix.spirometer.model.DetectorCompensation;
import com.fenix.spirometer.model.Province;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class FileParser {
    public static List<Province> parserProvince(InputStream in) {
        if (in == null) {
            return Collections.emptyList();
        }

        List<Province> provinces = null;
        Province prov = null;
        List<City> cities = null;
        City city = null;
        List<County> counties = null;
        County county = null;
        String tagName = null;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(in, "utf-8");
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        provinces = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if ("p".equals(tagName)) {
                            prov = new Province();
                            cities = new ArrayList<>();
                            int count = parser.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                String attName = parser.getAttributeName(i);
                                String attValue = parser.getAttributeValue(i);
                                if ("p_id".equals(attName)) {
                                    prov.setUid(attValue);
                                }
                            }
                        } else if ("pn".equals(tagName)) {
                            prov.setName(parser.nextText());
                        } else if ("c".equals(tagName)) {
                            city = new City();
                            city.setProUid(prov.getUid());
                            counties = new ArrayList<>();
                            int count = parser.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                String attName = parser.getAttributeName(i);
                                String attValue = parser.getAttributeValue(i);
                                if ("c_id".equals(attName)) {
                                    city.setUid(attValue);
                                }
                            }
                        } else if ("cn".equals(tagName)) {
                            city.setName(parser.nextText());
                        } else if ("d".equals(tagName)) {
                            county = new County();
                            county.setCitUid(city.getUid());
                            int count = parser.getAttributeCount();
                            for (int i = 0; i < count; i++) {
                                String attName = parser.getAttributeName(i);
                                String attValue = parser.getAttributeValue(i);
                                if ("d_id".equals(attName)) {
                                    county.setUid(attValue);
                                }
                            }
                            county.setName(parser.nextText());
                            counties.add(county);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();
                        if ("c".endsWith(tagName)) {
                            city.setCounties(counties);
                            cities.add(city);
                        } else if ("p".equals(tagName)) {
                            prov.setCities(cities);
                            provinces.add(prov);
                        }
                        break;
                    default:
                        break;
                }
                event = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return provinces;
    }

    public static List<DetectorCompensation> parserDetectorCompensations(InputStream in) {
        List<DetectorCompensation> data = new ArrayList<>();
        if (in == null) {
            return data;
        }

        String tagName = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(in, "utf-8");
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if ("item".equals(tagName)) {
                            DetectorCompensation compensation = new DetectorCompensation();
                            String attName = parser.getAttributeName(0);
                            String attValue = parser.getAttributeValue(0);
                            if ("range".equals(attName)) {
                                data.add(new DetectorCompensation(attValue, parser.nextText()));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();
                        break;
                    case XmlPullParser.START_DOCUMENT:
                    default:
                        break;
                }
                event = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }
}

