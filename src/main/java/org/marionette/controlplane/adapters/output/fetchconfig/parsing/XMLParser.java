package org.marionette.controlplane.adapters.output.fetchconfig.parsing;

import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import org.marionette.controlplane.adapters.output.fetchconfig.parsing.dto.MarionetteConfigDTO;
import org.marionette.controlplane.adapters.output.fetchconfig.parsing.exceptions.XMLMarionetteConfigParsingException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class XMLParser {

    public static MarionetteConfigDTO parseFromXMLString(String xml) throws XMLMarionetteConfigParsingException {

        try {
            JAXBContext context = JAXBContext.newInstance(MarionetteConfigDTO.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            MarionetteConfigDTO parsedConfig = (MarionetteConfigDTO) unmarshaller.unmarshal(new StreamSource( new StringReader(xml)));
            return parsedConfig;
        } catch (JAXBException exception) {
            throw new XMLMarionetteConfigParsingException("Impossible to parse the XML configuration");
        }
        
    }
    
}
