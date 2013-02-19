import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PathTest {

	Path p;
	int movementSpeed;
	int rotationSpeed;

	@Before
	public void setUp() {
		movementSpeed = 10;
		rotationSpeed = 45;
	}

	@Test
	public void testAngles() {

		p = new Path(new Place(0, 0), new Place(10, 10), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", 45, p.getRotationAngle(),0);

		p = new Path(new Place(10, 10), new Place(0, 0), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", 45-180, p.getRotationAngle(),0);

		p = new Path(new Place(10, 10), new Place(5, 5), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", 45-180, p.getRotationAngle(),0);

		p = new Path(new Place(5, 5), new Place(-5, -5), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", 45-180, p.getRotationAngle(),0);

		p = new Path(new Place(-5, 5), new Place(5, -5), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", -45, p.getRotationAngle(),0);

		p = new Path(new Place(0, 10), new Place(10, 0), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", -45, p.getRotationAngle(),0);

		p = new Path(new Place(0, 10), new Place(10, 0), 45, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", -90, p.getRotationAngle(),0);

		p = new Path(new Place(0, 10), new Place(10, 0), 245, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", 70, p.getRotationAngle(),0);

	}

}
