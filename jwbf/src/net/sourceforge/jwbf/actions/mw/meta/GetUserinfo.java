package net.sourceforge.jwbf.actions.mw.meta;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.actions.mw.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.Userinfo;
import net.sourceforge.jwbf.contentRep.mw.Version;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class GetUserinfo extends MWAction {

	private Userinfo userinfo;
	private final Logger log = Logger.getLogger(getClass());
	private String username = "";
	private final Vector<String> rights = new Vector<String>();
	private final Vector<String> groups = new Vector<String>();
	public GetUserinfo(Version v) throws VersionException {

		switch (v) {
		case MW1_09:
		case MW1_10:
			throw new VersionException("Not supportet by this version of MW");
			
		case MW1_11:
			try {
				msgs.add(new GetMethod(
						"/api.php?"
						+ "action=query&"
						+ "meta=userinfo&"
						+ "uiprop=" + URLEncoder.encode("blockinfo|hasmsg|groups|rights", MediaWikiBot.CHARSET) + "&"
						+ "format=xml"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			try {
				msgs.add(new GetMethod(
						"/api.php?"
						+ "action=query&"
						+ "meta=userinfo&"
						+ "uiprop=" + URLEncoder.encode("blockinfo|hasmsg|groups|rights|options|editcount|ratelimits", MediaWikiBot.CHARSET) + "&"
						+ "format=xml"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		

	}
	
	private void parse(final String xml) {
		log.debug(xml);
		SAXBuilder builder = new SAXBuilder();
		Element root = null;
		try {
			Reader i = new StringReader(xml);
			Document doc = builder.build(new InputSource(i));

			root = doc.getRootElement();
			findContent(root);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userinfo = new Userinfo(username, false, false, groups, rights);
	}
	
	/**
	 * @param s
	 *            the returning text
	 * @return empty string
	 * 
	 */
	public final String processAllReturningText(final String s)
			throws ProcessException {
		parse(s);
		return "";
	}

	
	public Userinfo getUserinfo() {
		return userinfo;
	}
	
	@SuppressWarnings("unchecked")
	protected void findContent(final Element root) {

		Iterator<Element> el = root.getChildren().iterator();

		while (el.hasNext()) {
			Element element = el.next();
			// blockinfo|hasmsg|groups|rights   <- MW 11
			if (element.getQualifiedName().equalsIgnoreCase("userinfo")) {
				username = element.getAttributeValue("name");
				
				
			} else if (element.getQualifiedName().equalsIgnoreCase("groups")){
				Iterator<Element> git = element.getChildren("g").iterator();
				while (git.hasNext()) {
					String gel = git.next().getTextTrim();
					groups.add(gel);
				}
			} else if (element.getQualifiedName().equalsIgnoreCase("rights")){
				
				Iterator<Element> rit = element.getChildren("r").iterator();
				while (rit.hasNext()) {
					String rel = rit.next().getTextTrim();
					
					rights.add(rel);
				}
			}
			findContent(element);
			
		}
		
		
	}
}