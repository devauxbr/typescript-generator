package cz.habarta.typescript.generator.ext;

import cz.habarta.typescript.generator.Settings;
import cz.habarta.typescript.generator.emitter.*;
import cz.habarta.typescript.generator.util.Utils;

import java.util.*;

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
        npmPackageDependencies.put("@angular/core", "^4.0.0");
        npmPackageDependencies.put("@angular/http", "^4.0.0");
        npmPackageDependencies.put("rxjs", "^5.4.1");
        features.npmPackageDependencies = npmPackageDependencies;
        return features;
    }

    @Override
    public void emitElements(Writer writer, Settings settings, boolean exportKeyword, TsModel model) {
        emitSharedPart(writer, settings);
        for (TsBeanModel bean : model.getBeans()) {
            if (bean.isJaxrsApplicationClientBean()) {
                final String clientName = bean.getName().getSimpleName();
                emitClient(writer, settings, exportKeyword, clientName);
            }
        }
    }

    private void emitSharedPart(Writer writer, Settings settings) {
        final List<String> template = Utils.readLines(getClass().getResourceAsStream("AngularClientExtension-shared.template.ts"));
        Emitter.writeTemplate(writer, settings, template, null);
    }

    private void emitClient(Writer writer, Settings settings, boolean exportKeyword, String clientName) {
        final List<String> template = Utils.readLines(getClass().getResourceAsStream("AngularClientExtension-client.template.ts"));
        final Map<String, String> replacements = new LinkedHashMap<>();
        replacements.put("\"", settings.quotes);
        replacements.put("/*export*/ ", exportKeyword ? "export " : "");
        replacements.put("$$RestApplicationClient$$", clientName);
        replacements.put("$$AngularRestApplicationClient$$", "Angular" + clientName);
        Emitter.writeTemplate(writer, settings, template, replacements);
    }
}
