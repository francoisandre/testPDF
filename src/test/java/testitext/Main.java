package testitext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class Main {

	@Test
	public void test() throws Exception {
		byte[] byteArray = Resources.toByteArray(Resources.getResource("sample.pdf"));
		InputStream readStream = ByteSource.wrap(byteArray).openStream();
		byte[] byteArray2 = watermarkPdf(readStream);
		InputStream readStream2 = ByteSource.wrap(byteArray2).openStream();
		byte[] byteArray3 = flattenPdf(readStream2);
		InputStream readStream3 = ByteSource.wrap(byteArray3).openStream();
		File file = File.createTempFile("test", ".pdf");
		save(readStream3, file);
		System.out.println("Sauvegardé dans " + file.getPath());

	}

	private byte[] flattenPdf(final InputStream inputStream) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PdfReader reader = new PdfReader(inputStream);
		PdfStamper stamper = new PdfStamper(reader, outputStream);
		stamper.setFormFlattening(true);

		stamper.close();
		reader.close();
		return outputStream.toByteArray();
	}

	public byte[] watermarkPdf(final InputStream inputStream) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PdfReader reader = new PdfReader(inputStream);
		PdfStamper stamper = new PdfStamper(reader, outputStream);
		int number_of_pages = reader.getNumberOfPages();
		int i = 0;
		Image watermark_image = Image.getInstance(Resources.toByteArray(Resources.getResource("confidentiel.jpg")));
		watermark_image.setAbsolutePosition(100, 200);
		watermark_image.scalePercent(50);
		PdfContentByte add_watermark;
		while (i < number_of_pages) {
			i++;
			add_watermark = stamper.getUnderContent(i);
			add_watermark.addImage(watermark_image);
		}
		stamper.close();
		reader.close();
		return outputStream.toByteArray();
	}

	private void save(final InputStream inputStream, final File file) {
		FileOutputStream outputStream = null;
		try {
			if (file.exists()) {
				file.delete();
			}
			outputStream = new FileOutputStream(file);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

}
