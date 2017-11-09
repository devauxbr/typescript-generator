package cz.habarta.typescript.generator.ext;

import cz.habarta.typescript.generator.*;
import org.junit.Assert;
import org.junit.Test;

public class AngularClientExtensionTest {

    @Test
    public void test() {
        final Settings settings = TestUtils.settings();
        settings.outputFileType = TypeScriptFileType.implementationFile;
        settings.outputKind = TypeScriptOutputKind.module;
        settings.generateJaxrsApplicationClient = true;
        settings.jaxrsNamespacing = JaxrsNamespacing.perResource;
        settings.extensions.add(new AngularClientExtension());
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(JaxrsApplicationTest.OrganizationApplication.class));
        final String errorMessage = "Unexpected output: " + output;

        Assert.assertTrue(errorMessage, output.contains("interface Organization"));
        Assert.assertTrue(errorMessage, output.contains("interface Address"));
        Assert.assertTrue(errorMessage, output.contains("interface Person"));
        Assert.assertTrue(errorMessage, output.contains("interface HttpClient"));

        Assert.assertTrue(errorMessage, output.contains("class OrganizationsResourceClient"));
        Assert.assertTrue(errorMessage, output.contains("class PersonResourceClient"));
        Assert.assertTrue(errorMessage, output.contains("type RestResponse<R> = Observable<R>;"));

        Assert.assertTrue(errorMessage, output.contains("class AngularHttpClient implements HttpClient"));
        Assert.assertTrue(errorMessage, output.contains("constructor(private http: AngularClient) {}"));
        Assert.assertTrue(errorMessage, output.contains("public request(requestConfig: { method: string; url: string; queryParams?: any; data?: any }): RestResponse<any>"));
        Assert.assertTrue(errorMessage, output.contains("Object.keys(requestConfig.queryParams).forEach(key"));
        Assert.assertTrue(errorMessage, output.contains("httpParams = httpParams.set(key, requestConfig.queryParams[key])"));
        Assert.assertTrue(errorMessage, output.contains("options.params = httpParams"));
        Assert.assertTrue(errorMessage, output.contains("options.body = requestConfig.data"));
        Assert.assertTrue(errorMessage, output.contains("return this.http.request(requestConfig.method, requestConfig.url, options)"));

        Assert.assertTrue(errorMessage, output.contains("class AngularOrganizationsResourceClient extends OrganizationsResourceClient"));
        Assert.assertTrue(errorMessage, output.contains("constructor(private http: AngularHttpClient)"));
        Assert.assertTrue(errorMessage, output.contains("class AngularPersonResourceClient extends PersonResourceClient"));
        Assert.assertTrue(errorMessage, output.contains("export class RestClientModule"));
        Assert.assertTrue(errorMessage, output.contains("provide: OrganizationsResourceClient"));
        Assert.assertTrue(errorMessage, output.contains("useClass: AngularOrganizationsResourceClient"));
        Assert.assertTrue(errorMessage, output.contains("provide: PersonResourceClient"));
        Assert.assertTrue(errorMessage, output.contains("useClass: AngularPersonResourceClient"));
    }

}