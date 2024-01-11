package org.mtr;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Task2 {
    public static void main(String[] args) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document docaddr = null;
        Document docadm = null;
        try {
            builder = factory.newDocumentBuilder();
            docaddr = builder.parse("AS_ADDR_OBJ.xml");
            docadm = builder.parse("AS_ADM_HIERARCHY.xml");
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            Map<String, String> listways = getObjWithWay(docaddr, xpath, "проезд");
            StringBuilder typename = null;
            for (Map.Entry<String, String> entry : listways.entrySet()) {
                String objectid = entry.getKey();
                typename = new StringBuilder(entry.getValue());
                String parentid = getParentIdAdm(docadm, xpath, objectid);
                do {
                    typename.insert(0, getObjById(docaddr, xpath, parentid));
                    parentid = getParentIdAdm(docadm, xpath, parentid);
                } while (!parentid.equals("0"));
                System.out.println(typename);
            }


        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    private static Map<String, String> getObjWithWay(Document doc, XPath xpath, String way) {
        String typename = "";
        Map<String, String> list = new HashMap<String, String>();

        try {
            XPathExpression expr =
                    xpath.compile("//OBJECT[@TYPENAME='" + way + "']");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                String objectid = nodes.item(i).getAttributes().getNamedItem("OBJECTID").getTextContent();
                typename = nodes.item(i).getAttributes().getNamedItem("TYPENAME").getTextContent();
                typename += " " + nodes.item(i).getAttributes().getNamedItem("NAME").getTextContent();
                String level = nodes.item(i).getAttributes().getNamedItem("LEVEL").getTextContent();
                if (!level.equals("1")) {
                    typename = ", " + typename;
                }
                list.put(objectid, typename);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String getParentIdAdm(Document doc, XPath xpath, String id) {
        String parentobjid = "";

        try {
            XPathExpression expr =
                    xpath.compile("//ITEM[@OBJECTID=" + id + "]");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                parentobjid = nodes.item(i).getAttributes().getNamedItem("PARENTOBJID").getTextContent();
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return parentobjid;
    }

    private static String getObjById(Document doc, XPath xpath, String id) {
        String typename = "";

        try {
            XPathExpression expr =
                    xpath.compile("//OBJECT[@OBJECTID=" + id + "]");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                typename = nodes.item(i).getAttributes().getNamedItem("TYPENAME").getTextContent();
                typename += " " + nodes.item(i).getAttributes().getNamedItem("NAME").getTextContent();
                String level = nodes.item(i).getAttributes().getNamedItem("LEVEL").getTextContent();
                if (!level.equals("1")) {
                    typename = ", " + typename;
                }
                break;
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return typename;
    }
}
