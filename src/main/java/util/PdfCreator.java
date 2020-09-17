package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;



@AllArgsConstructor
@Data
public class PdfCreator {

    private static final String OUTPUT_PATH = "output/";
    private static final String TITLE = "Document created by ";

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);


    public void createTemplate(String consumerName, String data) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_PATH +
                    consumerName.substring(consumerName.length() - 4) + "_" +
                    data + ".pdf"));

            document.open();
            document.addTitle(TITLE + consumerName);
            addContent(document, consumerName, data);
            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }

    }
    private void addContent(Document document, String consumerName, String data) throws DocumentException {
        Anchor anchor = new Anchor(TITLE + consumerName, catFont);
        anchor.setName(TITLE + consumerName);

        // Second parameter is the number of the chapter
        Chapter catPart = new Chapter(new Paragraph(anchor), 1);

        Paragraph subPara = new Paragraph("Personal data", subFont);
        Section subCatPart = catPart.addSection(subPara);
        HashMap <String, String> dataMap = parseData(data);
        for (String key : dataMap.keySet()) {
            subCatPart.add(new Paragraph(key + ": " + dataMap.get(key)));

        }


        document.add(catPart);

    }

    private HashMap<String, String> parseData(String data) {
        String[] params = data.split(" ");
        HashMap <String, String> dataMap = new HashMap<>();
        String[] paramsName = {"firstName", "lastName", "birthday", "passport"};
        int i = 0;
        for (String pName : paramsName) {
            dataMap.put(pName, params[i++]);
        }
        return dataMap;
    }

}
