package main;

import org.marc4j.*;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import java.io.*;
import java.sql.Connection;
import java.util.List;

/**
 * Created by Joshua Greben jgreben on 1/10/17.
 * Stanford University Libraries, DLSS
 */
class Transfom {

    static Record record(String marcfile) throws NullPointerException, MarcException, IOException {

        MarcWriter writer = new MarcXmlWriter(System.out, true);
        MarcFactory factory = MarcFactory.newInstance();

        try
        {
            InputStream input = new FileInputStream(marcfile);
            MarcReader reader = new MarcStreamReader(input);
            Connection conn = AuthDBConnection.open();

            while (reader.hasNext()) {
                Record record = reader.next();

                List fields = record.getDataFields();

                for (Object field : fields) {
                    DataField dataField = (DataField) field;

                    List subFieldList = dataField.getSubfields();

                    @SuppressWarnings("unchecked")
                    Object[] subfields = subFieldList.toArray(new Object[subFieldList.size()]);

                    for (Object subfield : subfields) {
                        Subfield sf = (Subfield) subfield;
                        char code = sf.getCode();
                        String codeStr = String.valueOf(code);
                        String data = sf.getData();

                        if (codeStr.equals("=")) {

                            String key = data.substring(2);
                            String authID = AuthIDfromDB.lookup(key, conn);

                            String[] tagNs = {"920", "921", "922"};
                            for (String n : tagNs) {
                                String uri = AuthURIfromDB.lookup(authID, n, conn);
                                if (uri.length() > 0) {
                                    dataField.addSubfield(factory.newSubfield('0', uri));
                                }
                            }
                            dataField.removeSubfield(sf);
                        }
                        if (codeStr.equals("?")) {
                            dataField.removeSubfield(sf);
                        }
                    }
                }

                writer.write(record);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }

        writer.close();
        return null;
    }
}
