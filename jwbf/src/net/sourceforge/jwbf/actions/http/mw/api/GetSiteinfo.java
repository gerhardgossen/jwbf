package net.sourceforge.jwbf.actions.http.mw.api;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.http.ProcessException;
import net.sourceforge.jwbf.actions.http.mw.MWAction;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;

import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class GetSiteinfo extends MWAction {

	private Siteinfo site = new Siteinfo();

	public GetSiteinfo() {
		GetMethod getMethod = new GetMethod(
				"/api.php?action=query&meta=siteinfo&siprop=general|namespaces|interwikimap&format=xml");
		System.out.println(getMethod.toString());
		msgs.add(getMethod);
	}

	/**
	 * @param s
	 *            the returning text
	 * @return empty string
	 * 
	 */
	public String processAllReturningText(final String s)
			throws ProcessException {
		parse(s);
		return "";
	}

	private void parse(final String xml) {
		System.out.println(xml);
		SAXBuilder builder = new SAXBuilder();
		Element root = null;
		try {
			Reader i = new StringReader(xml);
			Document doc = builder.build(new InputSource(i));

			root = doc.getRootElement();

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		findContent(root);
	}

	@SuppressWarnings("unchecked")
	private void findContent(final Element root) {

		Iterator<Element> el = root.getChildren().iterator();
		while (el.hasNext()) {
			Element element = (Element) el.next();
			if (element.getQualifiedName().equalsIgnoreCase("general")) {

				site.setMainpage(element
						.getAttributeValue("mainpage"));
				site.setBase(element.getAttributeValue("base"));
				site.setSitename(element
						.getAttributeValue("sitename"));
				site.setGenerator(element
						.getAttributeValue("generator"));
				site.setCase(element.getAttributeValue("case"));
				site.setRights(element.getAttributeValue("rights"));
			} else if (element.getQualifiedName().equalsIgnoreCase("ns")) {
				Integer id = Integer.parseInt(element.getAttributeValue("id"));
				String name = element.getText();
				site.addNamespace(id, name);

			} else if (element.getQualifiedName().equalsIgnoreCase("iw")) {
				if (element.getAttribute("prefix") != null) {
					String prefix = element
							.getAttributeValue("prefix");
					String name = element.getAttributeValue("url");
					site.addInterwiki(prefix, name);
				}
			} else {
				findContent(element);
			}
		}
	}

	public Siteinfo getSiteinfo() {
		return site;
	}

}
