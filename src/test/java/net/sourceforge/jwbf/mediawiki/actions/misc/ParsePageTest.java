package net.sourceforge.jwbf.mediawiki.actions.misc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import net.sourceforge.jwbf.core.contentRep.ParsedPage;

import org.junit.Test;

import com.google.common.io.Resources;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class ParsePageTest {

    @Test
    public void testParse() throws IOException {
        String s = Resources.toString(Resources.getResource("mediawiki/v1-23/parse.json"),
            StandardCharsets.UTF_8);
        ParsedPage page = ParsePage.parse(s);
        assertThat(page, notNullValue());
        assertThat(page.getTitle(), is("Hanover"));
        assertThat(page.getRevId(), is(633698142L));
        assertThat(page.getText(), containsString("Coat of Arms"));
        assertThat(page.getLangLinks(), is(not(empty())));
        assertThat(page.getLangLinks().get(0).getLang(), is(new Locale("af")));
        assertThat(page.getCategories(), is(not(empty())));
        assertThat(page.getCategories().get(0).getName(), is("CS1_German-language_sources_(de)"));
        assertThat(page.getCategories().get(0).isHidden(), is(true));
    }

}
