package passwords;


import commons.lib.main.filestructure.StructuredFile;
import passwords.pojo.CredentialDatum;

import java.util.ArrayList;
import java.util.List;

public final class StructuredFileHelper {
    private StructuredFileHelper() {

    }

    public static List<CredentialDatum> getCredentialData(StructuredFile file) {
        final List<CredentialDatum> credData = new ArrayList<>();
        for (List<String> fileDatum : file.getFileData()) {
            credData.add(new CredentialDatum(
                    fileDatum.get(4),
                    fileDatum.get(0),
                    fileDatum.get(1),
                    fileDatum.get(2),
                    fileDatum.get(3)
            ));
        }
        return credData;
    }

    public static StructuredFile getInstance(String separator, List<CredentialDatum> credData) {
        final StructuredFile structuredFile = new StructuredFile(separator);
        for (CredentialDatum credentialDatum : credData) {
            structuredFile.add(credentialDatum.getUrl());
            structuredFile.add(credentialDatum.getLogin());
            structuredFile.add(credentialDatum.getPassword());
            structuredFile.add(credentialDatum.getComments());
            structuredFile.add(credentialDatum.getHierarchy());
            structuredFile.newLine();
        }
        return structuredFile;
    }
}
