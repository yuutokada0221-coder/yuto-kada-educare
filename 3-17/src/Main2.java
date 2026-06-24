public class Main2 {
    public static void main(String[] args) {
        Student student = new Student();

        student.setName("田中太郎");
        student.setScore(85);
        System.out.println(student.getName() + "さんの点数： " + student.getScore() + "点");

        student.setScore(150);
        student.setName("");
    }
}