package net.sf.latexdraw.util;

import java.util.logging.Handler;
import net.sf.latexdraw.HelperTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@RunWith(MockitoJUnitRunner.class)
public class TestInjector implements HelperTest {
	Injector injector;
	@Mock Handler handler;

	@Before
	public void setUp() {
		Injector.LOGGER.addHandler(handler);
	}

	@Test
	public void testConstructor() {
		new Injector() {
			@Override
			protected void configure() throws IllegalAccessException, InstantiationException {
			}
		};
	}

	@Test
	public void testCreateRegisteredObject() {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
				bindAsEagerSingleton(D.class);
			}
		};

		assertNotNull(injector.getInstance(D.class));
		Mockito.verify(handler, Mockito.never()).publish(Mockito.any());
	}

	@Test
	public void testFailClassWithNoDefaultConst() {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
				bindAsEagerSingleton(K.class);
			}
		};
		assertNull(injector.getInstance(K.class));
		Mockito.verify(handler, Mockito.atLeastOnce()).publish(Mockito.any());
	}

	@Test
	public void testFailOnNullClass() {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
			}
		};
		assertNull(injector.getInstance(null));
		Mockito.verify(handler, Mockito.never()).publish(Mockito.any());
	}

	@Test
	public void testSingletonRegisteredObject() {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
				bindAsEagerSingleton(D.class);
			}
		};

		assertSame(injector.getInstance(D.class), injector.getInstance(D.class));
		Mockito.verify(handler, Mockito.never()).publish(Mockito.any());
	}

	@Test
	public void testFailOnNonConfigParam() {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
				bindAsEagerSingleton(A.class);
			}
		};
		assertNotNull(injector.getInstance(A.class));
		assertNull(injector.getInstance(A.class).b);
		Mockito.verify(handler, Mockito.atLeastOnce()).publish(Mockito.any());
	}

	@Test
	public void testInjectOnDeclaredField() throws NoSuchFieldException, IllegalAccessException {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
				bindAsEagerSingleton(D.class);
				bindAsEagerSingleton(C.class);
			}
		};

		assertNotNull(injector.getInstance(C.class));
		assertNotNull(injector.getInstance(D.class));
		assertSame(injector.getInstance(D.class), injector.getInstance(C.class).d);
		Mockito.verify(handler, Mockito.never()).publish(Mockito.any());
	}

	@Test
	public void testInjectOnInheritedField() throws NoSuchFieldException, IllegalAccessException {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
				bindAsEagerSingleton(D.class);
				bindAsEagerSingleton(E.class);
			}
		};

		assertNotNull(injector.getInstance(D.class));
		assertNotNull(injector.getInstance(E.class));
		assertSame(injector.getInstance(D.class), injector.getInstance(E.class).d);
		Mockito.verify(handler, Mockito.never()).publish(Mockito.any());
	}

	@Test
	public void testInjectOnBindCmd() throws NoSuchFieldException, IllegalAccessException {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
				bindAsEagerSingleton(G.class);
				bindWithCommand(F.class, G.class, g -> g);
				bindAsEagerSingleton(H.class);
			}
		};

		assertNotNull(injector.getInstance(G.class));
		assertNotNull(injector.getInstance(F.class));
		assertNotNull(injector.getInstance(H.class));
		assertSame(injector.getInstance(F.class), injector.getInstance(H.class).f);
		Mockito.verify(handler, Mockito.never()).publish(Mockito.any());
	}

	@Test
	public void testInjectOnCyclicDependency() throws NoSuchFieldException, IllegalAccessException {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
				bindAsEagerSingleton(A.class);
				bindAsEagerSingleton(B.class);
			}
		};

		assertNotNull(injector.getInstance(A.class));
		assertNotNull(injector.getInstance(B.class));
		assertSame(injector.getInstance(A.class), injector.getInstance(B.class).a);
		assertSame(injector.getInstance(B.class), injector.getInstance(A.class).b);
		Mockito.verify(handler, Mockito.never()).publish(Mockito.any());
	}

	@Test
	public void testDoNotCreateSingletonSeveralTimes() {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
				bindAsEagerSingleton(I.class);
			}
		};

		injector.getInstance(I.class);
		injector.getInstance(I.class);
		assertEquals(1, I.cpt);
		Mockito.verify(handler, Mockito.never()).publish(Mockito.any());
	}

	@Test
	public void testCanInjectItself() {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
				bindAsEagerSingleton(J.class);
			}
		};

		injector.getInstance(J.class);
		assertEquals(injector.getInstance(J.class), injector.getInstance(J.class).j);
		Mockito.verify(handler, Mockito.never()).publish(Mockito.any());
	}

	@Test
	public void testFailBindingOnOutClassAlreadyReg() {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
				bindAsEagerSingleton(C.class);
				bindAsEagerSingleton(E.class);
				bindWithCommand(C.class, E.class, obj -> obj);
			}
		};

		assertNotNull(injector.getInstance(C.class));
		assertNotNull(injector.getInstance(E.class));
		Mockito.verify(handler, Mockito.atLeastOnce()).publish(Mockito.any());
	}

	@Test
	public void testFailBindingOnSrcClassNotReg() {
		injector = new Injector() {
			@Override
			protected void configure() throws InstantiationException, IllegalAccessException {
				bindWithCommand(C.class, E.class, obj -> obj);
			}
		};

		Mockito.verify(handler, Mockito.atLeastOnce()).publish(Mockito.any());
	}

	static class A {
		@Inject B b;
	}

	static class B {
		@Inject A a;
	}

	static class C {
		@Inject D d;
	}

	static class D {
	}

	static class E extends C {

	}

	interface F {
	}

	static class G implements F {

	}

	static class H {
		@Inject F f;
	}

	static class I {
		static int cpt = 0;

		public I() {
			cpt++;
		}
	}

	static class J {
		@Inject J j;
	}

	static final class K {
		private K() {

		}
	}
}
