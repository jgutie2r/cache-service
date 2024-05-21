package edu.uv.cs.cache.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.uv.cs.cache.common.KeyNotFoundException;
import edu.uv.cs.cache.common.Pair;
import edu.uv.cs.cache.service.WebCache;

/**
 * This class is a Servlet.
 * It is not a REST API but is close to that
 */
public class WebCacheController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Gson g;
	private WebCache wcs;

	public WebCacheController(WebCache wcs) {
		g = new GsonBuilder().create();
		this.wcs = wcs;
	}

	/**
	 * Sends an error message to the client
	 */
	private void sendError(HttpServletResponse resp, int status, String msg) throws IOException {
		resp.setStatus(status);
		PrintWriter pw = resp.getWriter();
		pw.println(msg);
		pw.flush();
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
			Pair p = g.fromJson(br.readLine(), Pair.class);
			log("POST " + p.getKey() + "=" + p.getValue());

			// Business method call
			wcs.add(p);

			// Return the Pair in JSON format
			PrintWriter pw = resp.getWriter();
			pw.println(g.toJson(p, Pair.class));
			pw.flush();
			pw.close();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String key = req.getParameter("key");
		if (key != null) {
			try {

				// Business method call
				Pair p = wcs.find(key);

				PrintWriter pw = resp.getWriter();
				pw.println(g.toJson(p, Pair.class));
				pw.flush();
				pw.close();
			} catch (KeyNotFoundException ex) {
				sendError(resp, HttpServletResponse.SC_NOT_FOUND, key + " not found");
			}
		} else {
			// Business method call
			List<Pair> pares = wcs.findAll();

			PrintWriter pw = resp.getWriter();
			pw.println(g.toJson(pares));
			pw.flush();
			pw.close();
		}
	}

	/*
	 * protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
	 * throws ServletException, IOException {
	 * BufferedReader br = new BufferedReader(new
	 * InputStreamReader(req.getInputStream(), "UTF-8"));
	 * String key = br.readLine();
	 * try {
	 * wcs.delete(key);
	 * } catch (KeyNotFoundException ex) {
	 * sendError(resp, HttpServletResponse.SC_NOT_FOUND, key + " not found");
	 * }
	 * }
	 */
}
