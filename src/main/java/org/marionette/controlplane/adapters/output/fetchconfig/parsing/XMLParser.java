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
        
        // Debug: Print the XML string to see what we're actually trying to parse
        System.out.println("=== DEBUG: XML to parse ===");
        System.out.println("XML length: " + (xml != null ? xml.length() : "null"));
        System.out.println("XML content:");
        System.out.println(xml);
        System.out.println("=== END XML DEBUG ===");
        
        // Validate input
        if (xml == null || xml.trim().isEmpty()) {
            throw new XMLMarionetteConfigParsingException("XML string is null or empty");
        }
        
        try {
            JAXBContext context = JAXBContext.newInstance(MarionetteConfigDTO.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            MarionetteConfigDTO parsedConfig = (MarionetteConfigDTO) unmarshaller.unmarshal(new StreamSource(new StringReader(xml)));
            
            System.out.println("=== DEBUG: Parsing successful ===");
            return parsedConfig;
            
        } catch (JAXBException exception) {
            // Print the actual JAXB exception details
            System.err.println("=== JAXB Exception Details ===");
            System.err.println("Exception type: " + exception.getClass().getSimpleName());
            System.err.println("Exception message: " + exception.getMessage());
            System.err.println("Linked exception: " + exception.getLinkedException());
            
            if (exception.getLinkedException() != null) {
                System.err.println("Linked exception message: " + exception.getLinkedException().getMessage());
                exception.getLinkedException().printStackTrace();
            }
            
            exception.printStackTrace();
            System.err.println("=== End JAXB Exception Details ===");
            
            // Throw more informative exception
            throw new XMLMarionetteConfigParsingException(
                "Impossible to parse the XML configuration. JAXB Error: " + exception.getMessage() + 
                (exception.getLinkedException() != null ? 
                    ". Linked exception: " + exception.getLinkedException().getMessage() : "")
            );
        }
    }
    
}
