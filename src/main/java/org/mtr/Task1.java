package org.mtr;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task1 {
    public static void main(String[] args) {
        // Запуск с параметрами командной строки в формате:
        // 2010-01-01 1422396, 1450759, 1449192, 1451562
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document docaddr = null;
        Document docadm = null;
        try {
            builder = factory.newDocumentBuilder();
            docaddr = builder.parse("AS_ADDR_OBJ.xml");
            String paramdate = args[0];
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            for (int i = 1; i < args.length; i++) {
                String parm = args[i].replace(",", " ");
                String typename = getObjInDate(docaddr, xpath, paramdate, parm);
                System.out.println(parm + ": " + typename);
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String getObjInDate(Document doc, XPath xpath, String enddate, String id) {
        String typename = "";

        try {
            XPathExpression expr =
                    xpath.compile("//OBJECT[@OBJECTID=" + id + "]");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                String checkdate = nodes.item(i).getAttributes().getNamedItem("ENDDATE").getTextContent();
                Date dateend = new SimpleDateFormat("yyyy-MM-dd").parse(enddate);
                Date datecheck = new SimpleDateFormat("yyyy-MM-dd").parse(checkdate);
                if (datecheck.after(dateend)) {
                    typename = nodes.item(i).getAttributes().getNamedItem("TYPENAME").getTextContent();
                    typename += " " + nodes.item(i).getAttributes().getNamedItem("NAME").getTextContent();
                }
            }
        } catch (XPathExpressionException | ParseException e) {
            e.printStackTrace();
        }
        return typename;
    }

}