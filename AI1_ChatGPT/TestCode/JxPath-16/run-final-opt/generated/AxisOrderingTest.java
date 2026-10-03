import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.jdom.Element;
import org.jdom.Text;

public class AxisOrderingTest extends TestCase {

    public void testDomFollowingAxisKeepsParentBeforeDescendant() throws Exception {
        JXPathContext context = JXPathContext.newContext(createDomDocument());

        assertEquals("/vendor[1]/gap[1]",
                context.getPointer("//location[2]/following::node()[1]").asPath());
        assertEquals("/vendor[1]/product[1]",
                context.getPointer("//location[2]/following::node()[2]").asPath());
    }

    public void testDomPrecedingAxisUsesReverseDocumentOrder() throws Exception {
        JXPathContext context = JXPathContext.newContext(createDomDocument());

        assertEquals("/vendor[1]/location[1]/tail[2]",
                context.getPointer("//location[2]/preceding::node()[1]").asPath());
        assertEquals("/vendor[1]/location[1]/employeeCount[1]/text()[1]",
                context.getPointer("//location[2]/preceding::node()[3]").asPath());
    }

    public void testJdomFollowingAxisKeepsParentBeforeDescendant() {
        JXPathContext context = JXPathContext.newContext(createJdomDocument());

        assertEquals("/vendor[1]/gap[1]",
                context.getPointer("//location[2]/following::node()[1]").asPath());
        assertEquals("/vendor[1]/product[1]",
                context.getPointer("//location[2]/following::node()[2]").asPath());
    }

    public void testJdomPrecedingAxisUsesReverseDocumentOrder() {
        JXPathContext context = JXPathContext.newContext(createJdomDocument());

        assertEquals("/vendor[1]/location[1]/tail[2]",
                context.getPointer("//location[2]/preceding::node()[1]").asPath());
        assertEquals("/vendor[1]/location[1]/employeeCount[1]/text()[1]",
                context.getPointer("//location[2]/preceding::node()[3]").asPath());
    }

    private org.w3c.dom.Document createDomDocument() throws Exception {
        org.w3c.dom.Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();

        org.w3c.dom.Element vendor = document.createElement("vendor");
        document.appendChild(vendor);

        org.w3c.dom.Element firstLocation = document.createElement("location");
        vendor.appendChild(firstLocation);

        org.w3c.dom.Element employeeCount = document.createElement("employeeCount");
        employeeCount.appendChild(document.createTextNode("42"));
        firstLocation.appendChild(employeeCount);

        firstLocation.appendChild(document.createElement("tail"));
        firstLocation.appendChild(document.createElement("tail"));

        vendor.appendChild(document.createElement("location"));
        vendor.appendChild(document.createElement("gap"));

        org.w3c.dom.Element product = document.createElement("product");
        product.appendChild(document.createElement("name"));
        vendor.appendChild(product);

        return document;
    }

    private org.jdom.Document createJdomDocument() {
        Element vendor = new Element("vendor");

        Element firstLocation = new Element("location");
        Element employeeCount = new Element("employeeCount");
        employeeCount.addContent(new Text("42"));
        firstLocation.addContent(employeeCount);
        firstLocation.addContent(new Element("tail"));
        firstLocation.addContent(new Element("tail"));
        vendor.addContent(firstLocation);

        vendor.addContent(new Element("location"));
        vendor.addContent(new Element("gap"));

        Element product = new Element("product");
        product.addContent(new Element("name"));
        vendor.addContent(product);

        return new org.jdom.Document(vendor);
    }
}
