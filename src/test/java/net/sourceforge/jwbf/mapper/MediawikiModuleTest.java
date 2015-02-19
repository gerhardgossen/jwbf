package net.sourceforge.jwbf.mapper;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MediawikiModuleTest {
    private final static class Data {
        public boolean foo;
        public String bar;
    }

    @Test
    public void testBooleanDeserialization_true() throws IOException {
        String json = "{\"foo\": \"\", \"bar\": \"baz\"}";
        ObjectMapper mapper = new ObjectMapper().registerModule(new MediawikiModule());

        Data readTrue = mapper.readValue(json, Data.class);
        assertThat(readTrue.foo, is(true));
        assertThat(readTrue.bar, is("baz"));
    }

    @Test
    public void testBooleanDeserialization_false() throws IOException {
        String json = "{\"bar\": \"baz\"}";
        ObjectMapper mapper = new ObjectMapper().registerModule(new MediawikiModule());

        Data readTrue = mapper.readValue(json, Data.class);
        assertThat(readTrue.foo, is(false));
        assertThat(readTrue.bar, is("baz"));
    }

}
