package com.ideasquefluyen.selenium.xctrl.repository.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.ideasquefluyen.selenium.xctrl.repository.ConfigurationRepository;
import com.ideasquefluyen.selenium.xctrl.repository.impl.mapping.Configuration;

/**
 * Parse configuration XML
 *
 *
 * @author dmarafetti
 * @since 1.0.0
 *
 */
public class XMLConfigurationImpl implements ConfigurationRepository {


	private Configuration configuration;


	/**
	 *
	 * @param configPath
	 * @throws JAXBException
	 * @throws FactoryConfigurationError
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 */
	@Inject
	public XMLConfigurationImpl(@Named("selenium.config") String configPath) throws Exception {

		FileReader xmlReader = null;

		try {

			xmlReader = new FileReader(configPath);
			XMLStreamReader xmlStreamReader = XMLInputFactory.newFactory().createXMLStreamReader(xmlReader);

			// load schema
			//SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			//StreamSource streamSource = new StreamSource(ClassLoader.getSystemClassLoader().getResourceAsStream("com/vmbc/selenium/selenium/configuration.xsd"));
			//Schema schema = schemaFactory.newSchema(streamSource);

			// unmarshall
			JAXBContext context = JAXBContext.newInstance(Configuration.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			//unmarshaller.setSchema(schema);

			configuration = (Configuration) unmarshaller.unmarshal(xmlStreamReader);


		} catch(Exception ex) {

			throw new IllegalStateException(ex);

		} finally {

			if(xmlReader != null) xmlReader.close();
		}
	}



	/* (non-Javadoc)
	 * @see com.vmbc.selenium.repository.ConfigurationRepository#cleanUp()
	 */
	@Override
	public void cleanUp() {

		this.configuration.getTests().clear();
	}


	/*
	 * (non-Javadoc)
	 * @see com.vmbc.selenium.repository.ConfigurationRepository#get(java.lang.String)
	 */
	@Override
	public Map<String, String> get(String index) {

		return this.configuration.getTestByName(index).getParameters();
	}

}
