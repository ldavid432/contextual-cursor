package io.hydrox.contextualcursor.serialization;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.buildGson;
import com.github.ldavid432.contextualcursor.cursor.ContextualCursorDefinition;
import com.github.ldavid432.contextualcursor.cursor.Cursor;
import com.github.ldavid432.contextualcursor.cursor.JsonCursor;
import static com.github.ldavid432.contextualcursor.sprite.Sprite.resourceSprite;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.hydrox.contextualcursor.ContextualCursor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

// Not really a test
// TODO: Move to separate branch
public class Serialization
{
	Gson gson = buildGson(new GsonBuilder().create());

	@Test
	public void writeFile() throws IOException
	{
		File file = new File("C:\\Users\\ldavi\\IdeaProjects\\enriath-external-plugins\\src\\main\\resources\\io\\hydrox\\contextualcursor\\json\\local-cursors.json");
		file.getParentFile().mkdirs();
		file.createNewFile();
		FileWriter fileWriter = new FileWriter(file);
		List<Cursor> cursors = Arrays.stream(ContextualCursor.values()).map(c -> new JsonCursor(c.getSprite(), c.getMatcher())).collect(Collectors.toList());
		ContextualCursorDefinition writeDefinition = new ContextualCursorDefinition(
			cursors,
			resourceSprite().fileName("generic").build(),
			resourceSprite().fileName("blank").build()
		);
		fileWriter.write(gson.toJson(writeDefinition));
		fileWriter.close();
	}
}
