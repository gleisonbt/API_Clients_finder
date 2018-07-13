package aserg.ufmg.findAPIsClients;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.commons.io.FileUtils;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws IOException {
		Github github = new RtGithub("gleisonbt", "Aleister93");

		Request request;
		JsonArray items;
		int page = 1;
		int cont = 1;
		do {
			request = github.entry().uri().path("/search/repositories").queryParam("q", "language:java stars:>=100")
					.queryParam("sort", "stars").queryParam("per_page", "100").queryParam("page", "" + page)
					.back().method(Request.GET);

			System.out.println(request.uri().toString());
			items = request.fetch().as(JsonResponse.class).json().readObject().getJsonArray("items");

			for (JsonValue item : items) {
				JsonObject repoData = (JsonObject) item;
				
				String line = repoData.getString("full_name") + "," + repoData.getString("git_url") + ","
						+ repoData.getInt("stargazers_count");// + "," //stars
				
				//https://raw.githubusercontent.com/google/guava/master/pom.xml
				
				File dir = new File("/home/gleison/clients/" + repoData.getString("full_name").replaceFirst("/", "."));
				if (!dir.exists()) {
					dir.mkdir();
				}
				
				File pomFile = new File("/home/gleison/clients/" + repoData.getString("full_name").replaceFirst("/", ".") + "/pom.xml");
				File gradleFile = new File("/home/gleison/clients/" + repoData.getString("full_name").replaceFirst("/", ".") + "/build.gradle");
				
				
				try {
					FileUtils.copyURLToFile(new URL("https://raw.githubusercontent.com/" + repoData.getString("full_name") + "/master/pom.xml"), pomFile);
					if (!FileUtils.readFileToString(pomFile).contains("jcabi-github")) {
						dir.delete();
					}
					
				} catch (FileNotFoundException e) {
					dir.delete();
				}
				
				try {
					FileUtils.copyURLToFile(new URL("https://raw.githubusercontent.com/" + repoData.getString("full_name") + "/master/build.gradle"), gradleFile);
					
					if (!FileUtils.readFileToString(gradleFile).contains("jcabi-github")) {
						dir.delete();
					}
					
				} catch (FileNotFoundException e) {
					dir.delete();
				}
				
				
				System.out.println(cont++ + "" + "\t" + line);
			}

			page++;

			if (page > 10) { 
				break;
			}

		} while (items.size() == 100);

	}

}
