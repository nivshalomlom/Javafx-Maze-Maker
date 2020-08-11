
public class Vertex implements Cloneable {
		
		private int x, y;
		
		public Vertex(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
		
		public double distanceTo(Vertex vertex) {
			double XPart = Math.pow(this.x - vertex.x,2);
			double YPart = Math.pow(this.y - vertex.y,2);
			return Math.sqrt(XPart + YPart);
		}
		
		@Override
		public boolean equals(Object obj) {
			Vertex v2 = (Vertex)obj;
			if (v2.getX() == this.getX() && v2.getY() == this.getY())
				return true;
			return false;
		}
		
		@Override
		public Vertex clone() {
			return new Vertex(this.getX(), this.getY());
		}
		
		@Override
		public String toString() {
			return "(" + this.getX() + "," + this.getY() + ")";
		}
		
	}
