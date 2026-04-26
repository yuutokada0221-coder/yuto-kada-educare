public class Main4 {
    public static void main(String[] args) {
        int side1 = 3;
        int side2 = 4;
        int side3 = 5;
        if (side1 + side2 <= side3 || side1 + side3 <= side2 || side2 + side3 <= side1) {
            System.out.println("三角形ではない");
        } else if (side1 == side2 && side2 == side3) {
            System.out.println("正三角形");
        } else if ((side1 == side2 || side2 == side3 || side1 == side3) &&
                   (side1 * side1 + side2 * side2 == side3 * side3 ||
                    side1 * side1 + side3 * side3 == side2 * side2 ||
                    side2 * side2 + side3 * side3 == side1 * side1)) {
            System.out.println("直角二等辺三角形");
        } else if (side1 == side2 || side2 == side3 || side1 == side3) {
            System.out.println("二等辺三角形");
        } else if (side1 * side1 + side2 * side2 == side3 * side3 ||
                   side1 * side1 + side3 * side3 == side2 * side2 ||
                   side2 * side2 + side3 * side3 == side1 * side1) {
            System.out.println("直角三角形");
        } else {
            System.out.println("不等辺三角形");
        }
    }
}
