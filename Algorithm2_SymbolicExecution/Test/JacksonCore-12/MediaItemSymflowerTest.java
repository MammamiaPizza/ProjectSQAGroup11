package perf;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.*;
import static org.junit.Assert.*;

public class MediaItemSymflowerTest {
	@Test
	public void contentContent692() {
		MediaItem.Content expected = new MediaItem.Content();
		MediaItem.Content actual = new MediaItem.Content();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void contentGetBitrate693() {
		MediaItem.Content c = new MediaItem.Content();
		int expected = 0;
		int actual = c.getBitrate();

		assertEquals(expected, actual);
	}

	@Test
	public void contentGetCopyright694() {
		MediaItem.Content c = new MediaItem.Content();
		c.setCopyright("");
		String expected = "";
		String actual = c.getCopyright();

		assertEquals(expected, actual);
	}

	@Test
	public void contentGetDuration695() {
		MediaItem.Content c = new MediaItem.Content();
		long expected = 0L;
		long actual = c.getDuration();

		assertEquals(expected, actual);
	}

	@Test
	public void contentGetFormat696() {
		MediaItem.Content c = new MediaItem.Content();
		c.setFormat("");
		String expected = "";
		String actual = c.getFormat();

		assertEquals(expected, actual);
	}

	@Test
	public void contentGetHeight697() {
		MediaItem.Content c = new MediaItem.Content();
		int expected = 0;
		int actual = c.getHeight();

		assertEquals(expected, actual);
	}

	@Test
	public void contentGetPlayer698() {
		MediaItem.Content c = new MediaItem.Content();
		c.setPlayer(MediaItem.Player.FLASH);
		MediaItem.Player expected = MediaItem.Player.FLASH;
		MediaItem.Player actual = c.getPlayer();

		assertEquals(expected, actual);
	}

	@Test
	public void contentGetSize699() {
		MediaItem.Content c = new MediaItem.Content();
		long expected = 0L;
		long actual = c.getSize();

		assertEquals(expected, actual);
	}

	@Test
	public void contentGetTitle700() {
		MediaItem.Content c = new MediaItem.Content();
		c.setTitle("");
		String expected = "";
		String actual = c.getTitle();

		assertEquals(expected, actual);
	}

	@Test
	public void contentGetUri701() {
		MediaItem.Content c = new MediaItem.Content();
		c.setUri("");
		String expected = "";
		String actual = c.getUri();

		assertEquals(expected, actual);
	}

	@Test
	public void contentGetWidth702() {
		MediaItem.Content c = new MediaItem.Content();
		int expected = 0;
		int actual = c.getWidth();

		assertEquals(expected, actual);
	}

	@Test
	public void contentSetBitrate703() {
		MediaItem.Content c = new MediaItem.Content();
		int b = 0;
		c.setBitrate(b);

		MediaItem.Content cExpected = new MediaItem.Content();

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void contentSetCopyright704() {
		MediaItem.Content c2 = new MediaItem.Content();
		String c = "";
		c2.setCopyright(c);

		MediaItem.Content c2Expected = new MediaItem.Content();
		c2Expected.setCopyright("");

		assertTrue(EqualsBuilder.reflectionEquals(c2Expected, c2, false, null, true));
	}

	@Test
	public void contentSetDuration705() {
		MediaItem.Content c = new MediaItem.Content();
		long d = 0L;
		c.setDuration(d);

		MediaItem.Content cExpected = new MediaItem.Content();

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void contentSetFormat706() {
		MediaItem.Content c = new MediaItem.Content();
		String f = "";
		c.setFormat(f);

		MediaItem.Content cExpected = new MediaItem.Content();
		cExpected.setFormat("");

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void contentSetHeight707() {
		MediaItem.Content c = new MediaItem.Content();
		int h = 0;
		c.setHeight(h);

		MediaItem.Content cExpected = new MediaItem.Content();

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void contentSetPlayer708() {
		MediaItem.Content c = new MediaItem.Content();
		MediaItem.Player p = MediaItem.Player.FLASH;
		c.setPlayer(p);

		MediaItem.Content cExpected = new MediaItem.Content();
		cExpected.setPlayer(MediaItem.Player.FLASH);

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void contentSetSize709() {
		MediaItem.Content c = new MediaItem.Content();
		long s = 0L;
		c.setSize(s);

		MediaItem.Content cExpected = new MediaItem.Content();

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void contentSetTitle710() {
		MediaItem.Content c = new MediaItem.Content();
		String t = "";
		c.setTitle(t);

		MediaItem.Content cExpected = new MediaItem.Content();
		cExpected.setTitle("");

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void contentSetUri711() {
		MediaItem.Content c = new MediaItem.Content();
		String u = "";
		c.setUri(u);

		MediaItem.Content cExpected = new MediaItem.Content();
		cExpected.setUri("");

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void contentSetWidth712() {
		MediaItem.Content c = new MediaItem.Content();
		int w = 0;
		c.setWidth(w);

		MediaItem.Content cExpected = new MediaItem.Content();

		assertTrue(EqualsBuilder.reflectionEquals(cExpected, c, false, null, true));
	}

	@Test
	public void MediaItem713() {
		MediaItem.Content c = null;
		MediaItem expected = new MediaItem(null);
		MediaItem actual = new MediaItem(c);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void MediaItem714() {
		MediaItem expected = new MediaItem();
		MediaItem actual = new MediaItem();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void photoPhoto715() {
		String uri = "";
		String title = "";
		int w = 0;
		int h = 0;
		MediaItem.Size s = MediaItem.Size.LARGE;
		MediaItem.Photo expected = new MediaItem.Photo("", "", 0, 0, MediaItem.Size.LARGE);
		MediaItem.Photo actual = new MediaItem.Photo(uri, title, w, h, s);

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void photoPhoto716() {
		MediaItem.Photo expected = new MediaItem.Photo();
		MediaItem.Photo actual = new MediaItem.Photo();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void photoGetHeight717() {
		MediaItem.Photo p = new MediaItem.Photo(null, null, 0, 0, null);
		int expected = 0;
		int actual = p.getHeight();

		assertEquals(expected, actual);
	}

	@Test
	public void photoGetSize718() {
		MediaItem.Photo p = new MediaItem.Photo(null, null, 0, 0, MediaItem.Size.LARGE);
		MediaItem.Size expected = MediaItem.Size.LARGE;
		MediaItem.Size actual = p.getSize();

		assertEquals(expected, actual);
	}

	@Test
	public void photoGetTitle719() {
		MediaItem.Photo p = new MediaItem.Photo(null, "", 0, 0, null);
		String expected = "";
		String actual = p.getTitle();

		assertEquals(expected, actual);
	}

	@Test
	public void photoGetUri720() {
		MediaItem.Photo p = new MediaItem.Photo("", null, 0, 0, null);
		String expected = "";
		String actual = p.getUri();

		assertEquals(expected, actual);
	}

	@Test
	public void photoGetWidth721() {
		MediaItem.Photo p = new MediaItem.Photo(null, null, 0, 0, null);
		int expected = 0;
		int actual = p.getWidth();

		assertEquals(expected, actual);
	}

	@Test
	public void photoSetHeight722() {
		MediaItem.Photo p = new MediaItem.Photo(null, null, 0, 0, null);
		int h = 0;
		p.setHeight(h);

		MediaItem.Photo pExpected = new MediaItem.Photo(null, null, 0, 0, null);

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void photoSetSize723() {
		MediaItem.Photo p = new MediaItem.Photo(null, null, 0, 0, null);
		MediaItem.Size s = MediaItem.Size.LARGE;
		p.setSize(s);

		MediaItem.Photo pExpected = new MediaItem.Photo(null, null, 0, 0, MediaItem.Size.LARGE);

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void photoSetTitle724() {
		MediaItem.Photo p = new MediaItem.Photo(null, null, 0, 0, null);
		String t = "";
		p.setTitle(t);

		MediaItem.Photo pExpected = new MediaItem.Photo(null, "", 0, 0, null);

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void photoSetUri725() {
		MediaItem.Photo p = new MediaItem.Photo(null, null, 0, 0, null);
		String u = "";
		p.setUri(u);

		MediaItem.Photo pExpected = new MediaItem.Photo("", null, 0, 0, null);

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void photoSetWidth726() {
		MediaItem.Photo p = new MediaItem.Photo(null, null, 0, 0, null);
		int w = 0;
		p.setWidth(w);

		MediaItem.Photo pExpected = new MediaItem.Photo(null, null, 0, 0, null);

		assertTrue(EqualsBuilder.reflectionEquals(pExpected, p, false, null, true));
	}

	@Test
	public void getContent727() {
		MediaItem m = new MediaItem(new MediaItem.Content());
		MediaItem.Content expected = new MediaItem.Content();
		MediaItem.Content actual = m.getContent();

		assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false, null, true));
	}

	@Test
	public void setContent728() {
		MediaItem m = new MediaItem(null);
		MediaItem.Content c = new MediaItem.Content();
		m.setContent(c);

		MediaItem mExpected = new MediaItem(new MediaItem.Content());

		assertTrue(EqualsBuilder.reflectionEquals(mExpected, m, false, null, true));
	}
}
