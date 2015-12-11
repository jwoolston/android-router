package trikita.router;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;

import org.junit.Assert.*;
import org.junit.Test;

public class ViewRouterTest extends AndroidTestCase {

	public static class BazView extends View {
		public String uid;
		public String uriRemainder;
		public BazView(Context c, AttributeSet attrs) {
			super(c, attrs);
		}
	}
	public static class FooView extends View {
		public String uid;
		public String name;
		public FooView(Context c, AttributeSet attrs) {
			super(c, attrs);
		}
	}
	public static class BarView extends View {
		public String uid;
		public String uriRemainder;
		public BarView(Context c, AttributeSet attrs) {
			super(c, attrs);
		}
	}

	@Test
	public void testSimpleUris() {
		FrameLayout root = new FrameLayout(getContext());
		ViewRouter r = new ViewRouter(root)
			.add("/", BazView.class)
			.add("/foo", FooView.class)
			.add("/foo/bar/", BarView.class);

		r.route("/");
		assertEquals(BazView.class, root.getChildAt(0).getClass());

		assertFalse(r.route("//"));
		assertFalse(r.route("///"));
		assertFalse(r.route("//foo"));
		assertFalse(r.route("//foo/"));
		assertFalse(r.route("//foo//"));
		assertFalse(r.route("/foo/bar"));

		r.route("/foo/bar/");
		assertEquals(BarView.class, root.getChildAt(0).getClass());

		assertFalse(r.route("/foobar"));
	}

	@Test
	public void testUrisWithParams() {
		FrameLayout root = new FrameLayout(getContext());
		ViewRouter r = new ViewRouter(root)
			.add("/:uid", BazView.class)
			.add("/foo/:uid", FooView.class)
			.add("/foo/:uid/bar", BarView.class)
			.add("/foo/:uid/:name", FooView.class);

		r.route("/hello");
		assertEquals("hello", ((BazView) root.getChildAt(0)).uid);

		r.route("/foo/helloworld");
		assertEquals("helloworld", ((FooView) root.getChildAt(0)).uid);

		r.route("/foo/hello/world");
		assertEquals("hello", ((FooView) root.getChildAt(0)).uid);
		assertEquals("world", ((FooView) root.getChildAt(0)).name);

		r.route("/foo/hellohelloworld/bar");
		assertEquals("hellohelloworld", ((BarView) root.getChildAt(0)).uid);
	}

	@Test
	public void testIncorrectUris() {
		FrameLayout root = new FrameLayout(getContext());
		ViewRouter r = new ViewRouter(root)
			.add(":", FooView.class)
			.add("", BazView.class);

		assertFalse(r.route(""));
		assertFalse(r.route("/"));
		assertFalse(r.route(":"));
	}

	@Test
	public void testTrailingUris() {
		FrameLayout root = new FrameLayout(getContext());
		ViewRouter r = new ViewRouter(root)
			.add("/foo/...", BarView.class)
			.add("/...", BazView.class);

		r.route("/foo/hello/world");
		assertEquals(BarView.class, root.getChildAt(0).getClass());
		assertEquals("/hello/world", ((BarView) root.getChildAt(0)).uriRemainder);

		r.route("/hello/world");
		assertEquals(BazView.class, root.getChildAt(0).getClass());
		assertEquals("/hello/world", ((BazView) root.getChildAt(0)).uriRemainder);
	}
}
