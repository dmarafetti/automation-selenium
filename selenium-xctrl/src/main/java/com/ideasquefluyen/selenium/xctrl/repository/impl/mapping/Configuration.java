package com.ideasquefluyen.selenium.xctrl.repository.impl.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="configuration")
public class Configuration {

	private List<Test> tests = new ArrayList<Test>();

	@XmlElement(name="test")
	@XmlElementWrapper(name="tests")
	public List<Test> getTests() {
		return tests;
	}

	public void setTests(List<Test> tests) {
		this.tests = tests;
	}

	public Test getTestByName(String name) {

		Optional<Test> test = tests.stream()
								.filter(t -> t.getName().equals(name))
								.findFirst();

		if(!test.isPresent()) {

			throw new RuntimeException("Test configuration not found");
		}

		return test.get();
	}

}
