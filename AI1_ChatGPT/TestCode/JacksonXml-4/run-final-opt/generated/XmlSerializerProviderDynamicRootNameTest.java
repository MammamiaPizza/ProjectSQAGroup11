package com.fasterxml.jackson.dataformat.xml.misc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlSerializerProviderDynamicRootNameTest
{
    @Test
    public void dynamicRootNameIsUsedWhenSerializingTypedScalarValue() throws Exception
    {
        XmlMapper mapper = new XmlMapper();

        String xml = mapper.writer()
                .withRootName("rudy")
                .writeValueAsString("value");

        assertEquals("<rudy>value</rudy>", xml);
    }
}
