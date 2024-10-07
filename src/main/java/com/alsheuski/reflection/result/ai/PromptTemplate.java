package com.alsheuski.reflection.result.ai;

// todo - just for usage example. In the future will bw better to use LangChain4j
public class PromptTemplate {

  public String getMocksRequestPrompt(String deps) {
    var hasStatic = deps.contains("static ");
    var staticRule =
        hasStatic ? " - Mock all necessary static methods with Mockito.mockStatic\n" : "";

    var promptTemplate =
        "Technologies: used - Java, org.junit, org.mockito. Not used - jupiter\n"
            + "I have the next classes metadata:\n%s\n"
            + "Task: \n"
            + "Create mocks and all possible when - thenReturn conditions according to the dependencies between classes above.\n"
            + "Output requirements:\n"
            + " - Do not use 'any()' for mock something. Use the dedicated target type like anyString(), anyInt() e.t.c. \n"
            + "%s"
            + " - I need code only. Output pattern:\n"
            + "//--mocks section start\n"
            + "@Mock\n"
            + "private SomeClass someField;\n"
            + "@Mock\n"
            + "private SomeClass2 someField2;\n"
            + "//other mocks\n"
            + "//--mocks section stop\n"
            + "//--when-thenReturn section start\n"
            + "when(someClass.someMethod()).thenReturn(someOutput);\n"
            + "//--when-thenReturn section stop";
    return String.format(promptTemplate, deps, staticRule);
  }

  public String getTestRequestPrompt(String testableClass, String mocks) {
    var promptTemplate =
        "Technologies: used - Java, org.junit, org.mockito. Not used - jupiter\n"
            + "1. Task: You need to cover class below with JUnit tests. You need to define the most valuable public methods of it and cover with tests. \n"
            + "For example simple methods without any logic inside like getters/setters we do not need to cover.\n"
            + "2. Class for test coverage:\n"
            + "%s\n"
            + "3. Output Requirements: \n"
            + "- take in account all possible negative and positive cases, so your output should to have several test methods.\n"
            + "- use @RunWith(MockitoJUnitRunner.Silent.class) as class annotation and do not use MockitoAnnotations for init mocks.\n"
            + "- cover only public method from class above.\n"
            + "- do not use directly private methods from class above when you write tests for public methods.\n"
            + "%s"
            + "%s"
            + "- use @Rule public zeyt.LoggerRule loggerRule = new LoggerRule(); as global output class field.\n"
            + "- output pattern: <just code here>. No any additional text \n"
            + "- common mocks settings (if they are exists) for all test methods move to @Before or other common setup methods.\n"
            + "- for your defined public methods for coverage use ALL necessary mocks and ALL necessary when-thenReturn rules from example below to create effective mocks for specific project classes:\n"
            + "%s";

    var isAbstract = testableClass.contains("abstract ");
    var abstractRule =
        isAbstract
            ? "- mock abstract class with: mock(\n"
                + "    SomeAbstractClass.class,\n"
                + "    Mockito.withSettings()\n"
                + "        .useConstructor(arguments_if_needs)\n"
                + "        .defaultAnswer(Mockito.CALLS_REAL_METHODS));\n"
            : "";

    var hasSuper = testableClass.contains("super.");
    var ignoringRule =
        hasSuper
            ? "- for all methods with prefix 'super.' we need to ignore such calls with: Mockito.doNothing().when(mockForClassWhoCalls).superMethodNameHere();\n "
            : "";

    return String.format(promptTemplate, testableClass, abstractRule, ignoringRule, mocks);
  }
}
