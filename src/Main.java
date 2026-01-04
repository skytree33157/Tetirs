import javax.swing.JFrame;

public class Main extends JFrame{
    public Main(){
        setTitle("테트리스");

        //닫기 버튼 누르면 완전히 종료
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //게임패널을 윈도우에 추가
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        //패널의 크기에 맞춰서 윈도우 크기를 자동으로 조절
        pack();

        //창 크기 조절 불가능
        setResizable(false);

        //창을 화면 가운데에 배치
        setLocationRelativeTo(null);

        //창 시각화
        setVisible(true);
    }

    public static void main(String[] args){
        Database db=new Database();
        db.createDB();
        javax.swing.SwingUtilities.invokeLater(()-> {
            new Main();
        });
    }
}