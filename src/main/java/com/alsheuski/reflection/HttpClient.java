package com.alsheuski.reflection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    public static void main(String[] args) {
      try {
        // URL of the target server endpoint
        URL url = new URL("http://localhost:8080/api/v1/llm/text/generations?stream=false");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        JsonObject json = new JsonObject();
        json.addProperty("query", DATA);

        JsonObject modelOptions = new JsonObject();
        modelOptions.addProperty("model", "codestral:latest");

        json.add("model_options", modelOptions);

        // Convert the JSON object to a string

        // Send request
        try (OutputStream os = conn.getOutputStream()) {
          byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
          os.write(input, 0, input.length);
        }

        // Get response code
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Get the response body
        BufferedReader in;
        if (responseCode >= 200 && responseCode < 300) {
          // Successful response (2xx codes)
          in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
          // Error response (non-2xx codes)
          in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();


        JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
        // Print the response body
        System.out.println("Response Body: " + jsonObject.toString());

        // Clean up
        conn.disconnect();

      } catch (Exception e) {
        e.printStackTrace();
      }

  }

  private static final String DATA =
      "Technologies: used - Java, org.junit, org.mockito. Not used - jupiter\n"
          + "1. Task: You need to cover class below with JUnit tests. You need to define the most valuable public methods of it and cover with tests. \n"
          + "For example simple methods without any logic inside like getters/setters we do not need to cover.\n"
          + "2. Class for test coverage:\n"
          + "package zeyt.aml.core.account.service;\n"
          + "\n"
          + "import static java.util.Collections.emptyList;\n"
          + "import static java.util.stream.Collectors.toList;\n"
          + "\n"
          + "import java.util.List;\n"
          + "\n"
          + "import zeyt.model.BaseObjectUtil;\n"
          + "import zeyt.model.Id;\n"
          + "import zeyt.program.Account;\n"
          + "import zeyt.program.AccountHome;\n"
          + "import zeyt.sql.SqlBuilder;\n"
          + "import zeyt.sql.SqlStatement;\n"
          + "import zeyt.util.StringUtils;\n"
          + "\n"
          + "public class AMLAccountService_Impl implements AMLAccountService {\n"
          + "\n"
          + "  private final AccountHome accountHome;\n"
          + "\n"
          + "  public AMLAccountService_Impl() {\n"
          + "    accountHome = AccountHome.getInstance();\n"
          + "  }\n"
          + "\n"
          + "  public AMLAccountService_Impl(AccountHome accountHome) {\n"
          + "    this.accountHome = accountHome;\n"
          + "  }\n"
          + "\n"
          + "  @Override\n"
          + "  public List<Account> getNonSAAccountsWithPermission(Id compId, String permission) {\n"
          + "    if (compId == null || StringUtils.isBlank(permission)) {\n"
          + "      return emptyList();\n"
          + "    }\n"
          + "\n"
          + "    return getAccountIds(compId, permission).stream()\n"
          + "        .map(accountHome::getAccount)\n"
          + "        .filter(BaseObjectUtil::exists)\n"
          + "        .collect(toList());\n"
          + "  }\n"
          + "\n"
          + "  List<Id> getAccountIds(Id adminCompanyId, String permission) {\n"
          + "    var query =\n"
          + "        \"select distinct account.AccountId \"\n"
          + "            + \"from zeyt..tblAccount account with (NOLOCK) \"\n"
          + "            + \"join zeyt..tblSecurityProfile sec with (NOLOCK) on account.SecurityProfile = sec.ProfileId \"\n"
          + "            + \"join zeyt..tblSecurityProfileItem spi with (NOLOCK) on sec.ProfileId = spi.ProfileId \"\n"
          + "            + \"where account.CompId=? \"\n"
          + "            + \"and (spi.SecurityId=? or spi.SecurityId='ALL')\"\n"
          + "            + \"and sec.Deleted = 0 and account.Deleted = 0 and account.Locked = 0\"\n"
          + "            + \"and account.UserName <> 'sa' and account.Type <> 1\";\n"
          + "\n"
          + "    SqlStatement sqlStatement =\n"
          + "        SqlBuilder.query(query).add(adminCompanyId).add(permission).toSqlStatement();\n"
          + "    return accountHome.parseIdsFromSQL_v2(sqlStatement);\n"
          + "  }\n"
          + "}\n"
          + "\n"
          + "3. Output Requirements: \n"
          + "- take in account all possible negative and positive cases, so your output should to have several test methods.\n"
          + "- use @RunWith(MockitoJUnitRunner.Silent.class) as class annotation and do not use MockitoAnnotations for init mocks.\n"
          + "- cover only public method from class above.\n"
          + "- do not use directly private methods from class above when you write tests for public methods.\n"
          + "- use @Rule public zeyt.LoggerRule loggerRule = new LoggerRule(); as global output class field.\n"
          + "- output pattern: <just code here>. No any additional text \n"
          + "- common mocks settings (if they are exists) for all test methods move to @Before or other common setup methods.\n"
          + "- for your defined public methods for coverage use ALL necessary mocks and ALL necessary when-thenReturn rules from example below to create effective mocks for specific project classes:\n"
          + "//--mocks section start\n"
          + "@Mock\n"
          + "private AccountHome accountHome;\n"
          + "@Mock\n"
          + "private Account account;\n"
          + "@Mock\n"
          + "private SqlStatement sqlStatement;\n"
          + "@Mock\n"
          + "private Id id;\n"
          + "\n"
          + "//--mocks section stop\n"
          + "//--when-thenReturn section start\n"
          + "try (MockedStatic<AccountHome> accountHomeMockedStatic = Mockito.mockStatic(AccountHome.class)) {\n"
          + "    accountHomeMockedStatic.when(AccountHome::getInstance).thenReturn(accountHome);\n"
          + "    when(accountHome.getAccount(any(Id.class))).thenReturn(account);\n"
          + "    when(accountHome.parseIdsFromSQL_v2(any(SqlStatement.class))).thenReturn(Collections.emptyList());\n"
          + "    when(accountHome.parseIdsFromSQL_v2(isNull())).thenReturn(Collections.emptyList());\n"
          + "\n"
          + "\n"
          + "}\n"
          + "\n"
          + "try (MockedStatic<SqlBuilder> sqlBuilderMockedStatic = Mockito.mockStatic(SqlBuilder.class)) {\n"
          + "    sqlBuilderMockedStatic.when(() -> SqlBuilder.query(anyString())).thenReturn(Mockito.mock(SqlBuilder.class));\n"
          + "    SqlBuilder sqlBuilderMock = Mockito.mock(SqlBuilder.class);\n"
          + "    when(sqlBuilderMock.add(anyString())).thenReturn(sqlBuilderMock);\n"
          + "    when(sqlBuilderMock.add(any(Id.class))).thenReturn(sqlBuilderMock);\n"
          + "    when(sqlBuilderMock.toSqlStatement()).thenReturn(sqlStatement);\n"
          + "\n"
          + "    sqlBuilderMockedStatic.when(() -> SqlBuilder.query(isNull())).thenReturn(Mockito.mock(SqlBuilder.class));\n"
          + "    SqlBuilder sqlBuilderMockNull = Mockito.mock(SqlBuilder.class);\n"
          + "    when(sqlBuilderMockNull.add(anyString())).thenReturn(sqlBuilderMockNull);\n"
          + "    when(sqlBuilderMockNull.add(any(Id.class))).thenReturn(sqlBuilderMockNull);\n"
          + "    when(sqlBuilderMockNull.toSqlStatement()).thenReturn(sqlStatement);\n"
          + "\n"
          + "\n"
          + "}\n"
          + "\n"
          + "\n"
          + "try (MockedStatic<StringUtils> stringUtilsMockedStatic = Mockito.mockStatic(StringUtils.class)) {\n"
          + "    stringUtilsMockedStatic.when(() -> StringUtils.isBlank(anyString())).thenReturn(false);\n"
          + "    stringUtilsMockedStatic.when(() -> StringUtils.isBlank(isNull())).thenReturn(true);\n"
          + "\n"
          + "}\n"
          + "\n"
          + "try (MockedStatic<BaseObjectUtil> baseObjectUtilMockedStatic = Mockito.mockStatic(BaseObjectUtil.class)) {\n"
          + "    baseObjectUtilMockedStatic.when(() -> BaseObjectUtil.exists(any())).thenReturn(true);\n"
          + "    baseObjectUtilMockedStatic.when(() -> BaseObjectUtil.exists(isNull())).thenReturn(false);\n"
          + "}\n"
          + "//--when-thenReturn section stop\n";
}
