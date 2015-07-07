package com.brick.util.web;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <b>usage:</b>
 * @author zxb
 * @version 1 at Sep 22, 2005
 * <p/>
 */
public class VerifyCode {

	/**
	 * 
	 * @param request
	 * @param response
	 * @param sessionName
	 * @return
	 */
	public static void generate(HttpServletRequest request, HttpServletResponse response, String sessionName) throws Exception{
		
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("text/html");
        int width = 60;
        int height = 20;
        BufferedImage image = new BufferedImage(width, height, 1);
        Graphics g = image.getGraphics();
        Random random = new Random();
        g.setColor(new Color(250, 250, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Arial", 2, 18));
        
        String sRand = "";
        for(int i = 0; i < 4; i++)
        {
            String rand = String.valueOf(random.nextInt(10));
            sRand = sRand + rand;
            g.setColor(new Color(0, 0, 0));
            g.drawString(rand, 13 * i + 6, 16);
        }
        
        
        for(int i = 0; i < 40; i++)
        {
        	g.setColor(getRandColor(160, 200));
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y);
        }



        request.getSession().setAttribute(sessionName, sRand);
        g.dispose();
        ImageIO.write(image, "JPEG", response.getOutputStream());
	}
	
	private static Color getRandColor(int fc, int bc){
		
		Random random = new Random();
		if(fc>255) fc=255;
		if(bc>255) bc=255;
		int r=fc+random.nextInt(bc-fc);
		int g=fc+random.nextInt(bc-fc);
		int b=fc+random.nextInt(bc-fc);
		
		return new Color(r,g,b);
	}
}
