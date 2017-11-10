package cz.habarta.typescript.generator.ext;

import cz.habarta.typescript.generator.Settings;
import cz.habarta.typescript.generator.emitter.*;
import cz.habarta.typescript.generator.util.Utils;

import java.util.*;

import static java.lang.String.format;

public class AngularClientExtension extends EmitterExtension {

    @Override
    public EmitterExtensionFeatures getFeatures() {
        final EmitterExtensionFeatures features = new EmitterExtensionFeatures();
        features.generatesRuntimeCode = true;
        features.generatesModuleCode = true;
        features.worksWithPackagesMappedToNamespaces = true;
        features.generatesJaxrsApplicationClient = true;
        features.restResponseType = "Observable<R>";
        Map<String, String> npmPackageDependencies = new HashMap<>();
        npmPackageDependencies.put("@angular/core", "^5.0.0");
        npmPackageDependencies.put("@angular/common", "^5.0.0");
        npmPackageDependencies.put("rxjs", "^5.5.0");
        features.npmPackageDependencies = npmPackageDependencies;
        return features;
    }

    @Override
    public void emitElements(Writer writer, Settings settings, boolean exportKeyword, TsModel model) {
        emitImportAndHttpClient(writer, settings);
        final List<ClientProvider> clientProviders = new ArrayList<>();
        for (TsBeanModel bean : model.getBeans()) {
            if (bean.isJaxrsApplicationClientBean()) {
                final String clientName = bean.getName().getSimpleName();
                final String angularClientName = "Angular" + clientName;
                emitClient(writer, settings, exportKeyword, clientName, angularClientName);
                clientProviders.add(new ClientProvider(clientName, angularClientName));
            }
        }
        emitNgModule(writer, settings, clientProviders);
    }

    private void emitClient(Writer writer, Settings settings, boolean exportKeyword, String clientName, String angularClientName) {
        final List<String> template = Utils.readLines(getClass().getResourceAsStream("AngularClientExtension-client.template.ts"));
        final Map<String, String> replacements = new LinkedHashMap<>();
        replacements.put("\"", settings.quotes);
        replacements.put("/*export*/ ", exportKeyword ? "export " : "");
        replacements.put("$$RestApplicationClient$$", clientName);
        replacements.put("$$AngularRestApplicationClient$$", angularClientName);
        Emitter.writeTemplate(writer, settings, template, replacements);
    }

    private void emitImportAndHttpClient(Writer writer, Settings settings) {
        final List<String> template = Utils.readLines(getClass().getResourceAsStream("AngularClientExtension-http.template.ts"));
        Emitter.writeTemplate(writer, settings, template, null);
    }

    private void emitNgModule(Writer writer, Settings settings, List<ClientProvider> clientProviders) {
        final List<String> template = Utils.readLines(getClass().getResourceAsStream("AngularClientExtension-module.template.ts"));
        final Map<String, String> replacements = new LinkedHashMap<>();
        StringBuilder replacement = new StringBuilder();
        for (ClientProvider provider : clientProviders) {
            replacement.append("                {\n");
            replacement.append(format("                    provide: %s,\n", provider.provide));
            replacement.append(format("                    useClass: %s,\n", provider.useClass));
            replacement.append("                },\n");
        }
        replacements.put("$$ClientProviders$$", replacement.toString());
        Emitter.writeTemplate(writer, settings, template, replacements);
    }

    private static class ClientProvider {
        String provide;
        String useClass;

        ClientProvider(String provide, String useClass) {
            this.provide = provide;
            this.useClass = useClass;
        }
    }
}
