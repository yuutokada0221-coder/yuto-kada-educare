public class StudentManager {

    private String[] students = new String[5];
    private int count = 0;

    public void addStudent(String name) {
        if (count >= students.length) {
            throw new IllegalStateException("これ以上学生を追加できません。リストがいっぱいです。");
        }

        students[count] = name;
        count++;
    }

    public String getStudent(int id) {
        if (id < 0 || id >= students.length) {
            throw new IllegalArgumentException("無効な学生IDです: " + id);
        }

        if (students[id] == null) {
            throw new NullPointerException("学生ID " + id + " にデータがありません。");
        }

        return students[id];
    }

    public void updateStudent(int id, String name) {
        if (id < 0 || id >= students.length) {
            throw new IllegalArgumentException("無効な学生IDです: " + id);
        }

        students[id] = name;
    }
}
