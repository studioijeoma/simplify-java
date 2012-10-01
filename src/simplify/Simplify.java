/**

 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package simplify;

import java.util.ArrayList;

public class Simplify {
	public static float[][] simplify(float[][] points, float tolerance) {
		float sqTolerance = tolerance * tolerance;

		return simplifyDouglasPeucker(points, sqTolerance);
	}

	public static float[][] simplify(float[][] points, float tolerance,
			boolean highestQuality) {
		float sqTolerance = tolerance * tolerance;

		if (!highestQuality)
			points = simplifyRadialDistance(points, sqTolerance);

		points = simplifyDouglasPeucker(points, sqTolerance);

		return points;
	}

	// distance-based simplification
	public static float[][] simplifyRadialDistance(float[][] points,
			float sqTolerance) {
		int len = points.length;

		float[] point = new float[2];
		float[] prevPoint = points[0];

		ArrayList<float[]> newPoints = new ArrayList<float[]>();
		newPoints.add(prevPoint);

		for (int i = 1; i < len; i++) {
			point = points[i];

			if (getSquareDistance(point, prevPoint) > sqTolerance) {
				newPoints.add(point);
				prevPoint = point;
			}
		}

		if (!prevPoint.equals(point)) {
			newPoints.add(point);
		}

		return newPoints.toArray(new float[newPoints.size()][2]);
	}

	// simplification using optimized Douglas-Peucker algorithm with recursion
	// elimination
	public static float[][] simplifyDouglasPeucker(float[][] points,
			float sqTolerance) {
		int len = points.length;

		Integer[] markers = new Integer[len];

		Integer first = 0;
		Integer last = len - 1;

		float maxSqDist;
		float sqDist;
		int index = 0;

		ArrayList<Integer> firstStack = new ArrayList<Integer>();
		ArrayList<Integer> lastStack = new ArrayList<Integer>();

		ArrayList<float[]> newPoints = new ArrayList<float[]>();

		markers[first] = markers[last] = 1;
 
		while (last != null) {
			maxSqDist = 0;

			for (int i = first + 1; i < last; i++) {
				sqDist = getSquareSegmentDistance(points[i], points[first],
						points[last]);

				if (sqDist > maxSqDist) {
					index = i;
					maxSqDist = sqDist;
				}
			}

			if (maxSqDist > sqTolerance) {
				markers[index] = 1;

				firstStack.add(first);
				lastStack.add(index);

				firstStack.add(index);
				lastStack.add(last);
			}

			if (firstStack.size() > 1)
				first = firstStack.remove(firstStack.size() - 1);
			else
				first = null;

			if (lastStack.size() > 1)
				last = lastStack.remove(lastStack.size() - 1);
			else
				last = null;
		}

		for (int i = 0; i < len; i++) {
			if (markers[i] != null)
				newPoints.add(points[i]);
		}

		return newPoints.toArray(new float[newPoints.size()][2]);
	}

	public static float getSquareDistance(float[] p1, float[] p2) {
		float dx = p1[0] - p2[0], dy = p1[1] - p2[1], dz = p1[2] - p2[2];
		return dx * dx + dy * dy + dz * dz;
	}

	// square distance from a point to a segment
	public static float getSquareSegmentDistance(float[] p, float[] p1,
			float[] p2) {
		float x = p1[0], y = p1[1], z = p1[2];

		float dx = p2[0] - x, dy = p2[1] - y, dz = p2[2] - z;

		float t;

		if (dx != 0 || dy != 0 || dz != 0) {
			t = ((p[0] - x) * dx + (p[1] - y) * dy) + (p[2] - z) * dz
					/ (dx * dx + dy * dy + dz * dz);

			if (t > 1) {
				x = p2[0];
				y = p2[1];
				z = p2[2];

			} else if (t > 0) {
				x += dx * t;
				y += dy * t;
				z += dz * t;
			}
		}

		dx = p[0] - x;
		dy = p[1] - y;
		dz = p[2] - z;

		return dx * dx + dy * dy + dz * dz;
	}
}
