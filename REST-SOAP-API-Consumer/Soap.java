package ae.dubaipolice.dpappstore.identityserviceconsumer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ae.dubaipolice.smartservices.dpappstore.utils.AppConstants;
import ae.dubaipolice.smartservices.dpappstore.utils.Utilities;

public class IdentityUserStoreManagerServiceConsumer {

	
	public static List<String> getAccessRoles(String NTLogin) throws Exception {
		NTLogin = AppConstants.DomainName + NTLogin;

		List<String> list = new ArrayList<>();
		list.add(AppConstants.DEFAULT_ROLE_NAME);

		
		MessageFactory factory = MessageFactory.newInstance();
		SOAPMessage soapMsg = factory.createMessage();
		SOAPPart part = soapMsg.getSOAPPart();
		SOAPEnvelope env = part.getEnvelope();
		
		env.addNamespaceDeclaration("ser", "http://service.ws.um.carbon.wso2.org");
		SOAPBody body = (SOAPBody) env.getBody();
		SOAPElement envBody = body.addChildElement("getRoleListOfUser", "ser");
		envBody.addChildElement("userName", "ser").addTextNode(NTLogin);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapMsg.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8");

		String response = new Utilities().postXML(message,AppConstants.USER_STORE_MANAGER_SERVICE_ENDPOINT,"getRoleListOfUser");

		if (response != null && response.contains("getRoleListOfUserResponse")) {
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			dbFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
			
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();			
			Document doc = dBuilder.parse(new ByteArrayInputStream(response.getBytes()));			
			NodeList responseList = doc.getElementsByTagName("ns:return");
			
			responseList.getLength();
			for (int counter = 0; counter < responseList.getLength(); counter++) {
				Node nNode = responseList.item(counter);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String logAccess = eElement.getTextContent();
					if (logAccess.contains(AppConstants.CONTEXT_NAME)) 
					{
						list.add(logAccess.trim().split("_")[1]);
					}
					if(logAccess.trim().contains(AppConstants.EXEMPT_ROLE_NAME.trim())){
						list.remove(AppConstants.DEFAULT_ROLE_NAME);
					}
				}
			}
		}
		return list;
	}		
}
