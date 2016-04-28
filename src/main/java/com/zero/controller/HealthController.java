package com.zero.controller;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@EnableAutoConfiguration
public class HealthController {
	
	public static PDDocument document = new PDDocument();

	@RequestMapping("/")
	@ResponseBody
	public String home(){
		return "Server up at" + new Date(System.currentTimeMillis());
	}
	
	@RequestMapping(value="/home", method=RequestMethod.GET)
	public String homePage(){
		return "index";
	}
	
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	@ResponseBody
	public String uploadFile(@RequestParam("file") String file){
		
		if(!file.isEmpty()){
			try {
				byte[] bytes = DatatypeConverter.parseBase64Binary(file.replaceAll("data:image/.+;base64,", ""));
				System.out.println(bytes);
				createPDF(bytes);
				return "success";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "error processing file";
			}
			
		}else{
			return "empty file";
		}
		
	}
	
	@RequestMapping(value="/pdf", method=RequestMethod.GET)
	@ResponseBody
	public void fetchPDF(HttpServletRequest request, HttpServletResponse response) throws IOException, COSVisitorException{
		try {
			InputStream contentStream = null;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			document.save(byteArrayOutputStream);
			document.close();
			byte[] file = byteArrayOutputStream.toByteArray();
			response.setContentType("application/pdf");
			contentStream = new ByteArrayInputStream(file);
			response.setContentLength(file.length);
			FileCopyUtils.copy(contentStream, response.getOutputStream());
		} catch (IOException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
		}
	}
	
	public static void createPDF(byte[] rawImage) throws IOException{
		ClassLoader classLoader = HealthController.class.getClassLoader();
		document = PDDocument.load(new File(classLoader.getResource("anthem.pdf").getFile()));
		//we know there are 2 pages
		PDPage page = (PDPage)document.getDocumentCatalog().getAllPages().get(1);
		try {
			BufferedImage bufImage = ImageIO.read(new ByteArrayInputStream(rawImage));
			bufImage = Thumbnails.of(bufImage).forceSize(200, 100).asBufferedImage();
			PDXObjectImage image = new PDPixelMap(document, bufImage);
			PDPageContentStream content = new PDPageContentStream(document, page, true, true);
			content.drawImage(image, 70, 570);
			content.drawImage(image, 400, 300);
			content.close();
			
			//print on first page
			BufferedImage bufImage1 = ImageIO.read(new ByteArrayInputStream(rawImage));
			bufImage1 = Thumbnails.of(bufImage1).forceSize(300, 150).asBufferedImage();
			PDXObjectImage image1 = new PDPixelMap(document, bufImage1);
			page = (PDPage)document.getDocumentCatalog().getAllPages().get(0);
			content = new PDPageContentStream(document, page, true, true);
			content.drawImage(image1, 70, 100);
			content.close();
			//document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
