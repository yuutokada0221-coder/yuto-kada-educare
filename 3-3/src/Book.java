public class Book {
    String title;
    boolean isLent;

    public void setTitle(String title){
        this.title = title;
        System.out.println("「" + this.title + "」を登録しました");
    }
    public String getTitle() {
        return this.title;
    }    
    public void lend(){
        if (this.isLent == true){
            System.out.println("申し訳ありません。この本は貸出中です");
        }else{
            this.isLent = true;
            System.out.println("本を貸し出しました"); 
        } 
    }
    public void returnBook(){
        if (this.isLent == true){
            this.isLent = false;
            System.out.println("本が返却されました");
        }else{
            System.out.println("この本は貸出中ではありません");
        }
    }
}

        
        
        
    
