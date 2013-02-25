package org.yoniehax.tankoid;
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

		p = new Path(new Place(0, 0), new Place(0, 1), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", 0, p.getRotationAngle(),0);

		p = new Path(new Place(0, 0), new Place(1, 1), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", 45, p.getRotationAngle(),0);

		p = new Path(new Place(0, 0), new Place(1, 0), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", 90, p.getRotationAngle(),0);

		p = new Path(new Place(0, 0), new Place(1, -1), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", 135, p.getRotationAngle(),0);

		p = new Path(new Place(0, 0), new Place(0, -1), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", 180, p.getRotationAngle(),0);

		p = new Path(new Place(0, 0), new Place(-1, -1), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", -135, p.getRotationAngle(),0);

		p = new Path(new Place(0, 0), new Place(-1, 0), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", -90, p.getRotationAngle(),0);

		p = new Path(new Place(0, 0), new Place(-1, 1), 0, movementSpeed, rotationSpeed);
		assertEquals("Angle not correct:", -45, p.getRotationAngle(),0);


	}

}
