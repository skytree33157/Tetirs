import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class GamePanel extends JPanel{

    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    public static final int BLOCK_SIZE = 30;

    private int[][] board = new int [HEIGHT][WIDTH];
    private Color[][] boardColor = new Color [HEIGHT][WIDTH];
    private Timer timer;
    public static int score = 0;
    private boolean isGameOver = false;
    private boolean isGaming = false;

    private Block nextBlock;
    private Block block;
    private Block holdBlock;
    private boolean checkHoldBlock = true;

    private Database db;
    private JPanel loginPanel;
    private JTextField idField;
    private JPasswordField pwField;

    public GamePanel(){
        setBackground(Color.BLACK);
        //패널 크기
        setPreferredSize(new Dimension(WIDTH*BLOCK_SIZE+150, HEIGHT*BLOCK_SIZE));

        block=new Block((int)(Math.random()*7));
        nextBlock=new Block((int)(Math.random()*7));

        setLayout(new GridBagLayout());

        db=new Database();
        db.createTable();

        //키 리스너 추가
        addKeyListener(new Keyboard());
        setFocusable(true);
        requestFocusInWindow();

        timer = new Timer(600, new ActionListener(){
            @Override//주기적으로 실행
            public void actionPerformed(ActionEvent e){
                if(!checkCollision(block.getX(), block.getY()+1))
                    block.moveDown();
                else{
                    //끝에 도달하면 보드에 저장
                    endBlock();
                    checkHoldBlock=true;
                    //한 라인이 꽉 찼는지 확인
                    checkLine();

                    block = nextBlock;
                    block.setX(4);
                    block.setY(0);
                    nextBlock = new Block((int)(Math.random()*7));

                    if(checkCollision(block.getX(), block.getY())){
                        isGameOver = true;
                        timer.stop();
                        //db.saveScore(UserID,score);
                    }
                }
                repaint();
            }
        });
        loginScreen();
    }

    private void loginScreen(){
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(4,1,5,5));
        loginPanel.setBackground(new Color(255,255,255,0));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setForeground(Color.white);
        //titleLabel.setOpaque(true);
        //titleLabel.setBackground(Color.white);

        idField=new JTextField(10);
        idField.setBorder(BorderFactory.createTitledBorder("Username"));
        pwField=new JPasswordField(10);
        pwField.setBorder(BorderFactory.createTitledBorder("Password"));

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton loginBtn = new JButton("login");
        JButton registerBtn = new JButton("register");
        btnPanel.setOpaque(false);

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        loginPanel.add(titleLabel);
        loginPanel.add(idField);
        loginPanel.add(pwField);
        loginPanel.add(btnPanel);

        loginBtn.addActionListener(e->{
            String id=idField.getText();
            String pw=new String(pwField.getPassword());
            if(db.login(id,pw)){
                startGame();
            }
            else{
                JOptionPane.showMessageDialog(this,"로그인 실패");
            }
        });

        registerBtn.addActionListener(e -> {
            String id=idField.getText();
            String pw=new String(pwField.getPassword());
            if(db.register(id,pw)){
                JOptionPane.showMessageDialog(this, "회원가입 성공, 로그인 해주세요");
            }
            else{
                JOptionPane.showMessageDialog(this,"이미 존재하는 아이디");
            }
        });

        add(loginPanel);
    }

    private void startGame(){
        remove(loginPanel);
        //레이아웃 갱신
        revalidate();
        repaint();
        isGaming=true;
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        //격자
        g.setColor(Color.darkGray);
        for(int i=0;i<=HEIGHT;i++)
            g.drawLine(0, i*BLOCK_SIZE, WIDTH*BLOCK_SIZE, i*BLOCK_SIZE);
        for(int i=0;i<=WIDTH;i++)
            g.drawLine(i*BLOCK_SIZE, 0, i*BLOCK_SIZE, HEIGHT*BLOCK_SIZE);

        for(int row=0;row<HEIGHT;row++){
            for(int col=0;col<WIDTH;col++){
                if(board[row][col]==1){
                    g.setColor(boardColor[row][col]);
                    g.fillRect(col*BLOCK_SIZE, row*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

                    g.setColor(Color.black);
                    g.drawRect(col*BLOCK_SIZE, row*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        //블록 소환
        if(block!=null&&isGaming){
            int[][] shape = block.getCurShape();
            g.setColor(block.getCurColor());

            for(int row=0;row< shape.length;row++){
                for(int col=0;col<shape[row].length;col++){
                    if(shape[row][col]==1){
                        int blockX = (block.getX()+col)*BLOCK_SIZE;
                        int blockY = (block.getY()+row)*BLOCK_SIZE;

                        g.fillRect(blockX, blockY, BLOCK_SIZE, BLOCK_SIZE);

                        //블록 내부 선
                        g.setColor(Color.black);
                        g.drawRect(blockX,blockY, BLOCK_SIZE, BLOCK_SIZE);
                        g.setColor(block.getCurColor());
                    }
                }
            }
        }

        //사이드 바
        int sideX = WIDTH*BLOCK_SIZE+20;
        g.setColor(Color.white);

        g.drawString("SCORE", sideX, 50);
        g.drawString(String.valueOf(score), sideX, 80);

        g.drawString("Next Block", sideX, 150);

        if(nextBlock!=null&&isGaming) {
            int[][] nextShape = nextBlock.getCurShape();
            g.setColor(nextBlock.getCurColor());

            for (int row = 0; row < nextShape.length; row++) {
                for (int col = 0; col < nextShape[row].length; col++) {
                    if (nextShape[row][col] == 1) {
                        int nextBlockX = col * BLOCK_SIZE + sideX;
                        int nextBlockY = row * BLOCK_SIZE + 180;
                        g.fillRect(nextBlockX, nextBlockY, BLOCK_SIZE, BLOCK_SIZE);
                        g.setColor(Color.black);
                        g.drawRect(nextBlockX, nextBlockY, BLOCK_SIZE, BLOCK_SIZE);
                        g.setColor(nextBlock.getCurColor());
                    }
                }
            }
        }
        g.setColor(Color.white);
        g.drawString("Hold Block", sideX, 300);

        if(holdBlock!=null) {
            int[][] holdShape = holdBlock.getCurShape();
            g.setColor(holdBlock.getCurColor());

            for (int row = 0; row < holdShape.length; row++) {
                for (int col = 0; col < holdShape[row].length; col++) {
                    if (holdShape[row][col] == 1) {
                        int holdBlockX = col * BLOCK_SIZE + sideX;
                        int holdBlockY = row * BLOCK_SIZE + 350;
                        g.fillRect(holdBlockX, holdBlockY, BLOCK_SIZE, BLOCK_SIZE);
                        g.setColor(Color.black);
                        g.drawRect(holdBlockX, holdBlockY, BLOCK_SIZE, BLOCK_SIZE);
                        g.setColor(holdBlock.getCurColor());
                    }
                }
            }
        }

        //게임오버
        if(isGameOver){
            g.setColor(new Color(0,0,0,150));
            g.fillRect(0,0,getWidth(),getHeight());

            g.setColor(Color.white);
            g.drawString("Game Over", getWidth()/2, getHeight()/2);
            g.drawString("Score: "+ score, getWidth()/2, getHeight()/2+50);
        }
    }

    class Keyboard extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT && !checkCollision(block.getX()-1, block.getY()))
                block.moveLeft();
            else if (key == KeyEvent.VK_RIGHT && !checkCollision(block.getX()+1, block.getY()))
                block.moveRight();
            else if (key == KeyEvent.VK_DOWN && !checkCollision(block.getX(), block.getY()+1))
                block.moveDown();
            else if (key == KeyEvent.VK_UP)
                wallKick();
            else if (key==KeyEvent.VK_SPACE && checkHoldBlock){
                if(holdBlock==null){
                    holdBlock=new Block(block.getBlockNum());
                    block=nextBlock;
                    nextBlock=new Block((int) (Math.random()*7));
                }
                else{
                    Block tmp=holdBlock;
                    holdBlock=new Block(block.getBlockNum());
                    block=tmp;
                }
                block.setX(4);
                block.setY(0);
                checkHoldBlock=false;
            }
            repaint();
        }
    }

    private void wallKick(){
        block.rotate();
        if(!checkCollision(block.getX(), block.getY()))
            return;

        int[][] offsets={
                {1,0}, {-1,0}, {0,-1}, {1,-1}, {-1,-1}, {2,0}, {-2,0}
        };

        for(int[] offset: offsets){
            int kickX=block.getX()+offset[0];
            int kickY=block.getY()+offset[1];
            if(!checkCollision(kickX,kickY)){
                block.setX(kickX);
                block.setY(kickY);
                return;
            }
        }
        block.rotate();block.rotate();block.rotate();
    }
    private boolean checkCollision(int X, int Y){
        int[][] shape = block.getCurShape();

        for(int row=0;row<shape.length;row++){
            for(int col=0;col<shape[row].length;col++){
                if(shape[row][col]==1){
                    int blockX = X + col;
                    int blockY = Y + row;

                    if(blockX<0||blockX>=WIDTH||blockY>=HEIGHT)
                        return true;
                    if(blockY>=0 && board[blockY][blockX]!=0 )
                        return true;
                }
            }
        }
        return false;
    }

    private void endBlock(){
        int[][] shape = block.getCurShape();
        Color color = block.getCurColor();

        for(int row=0;row<shape.length;row++){
            for(int col=0;col<shape[row].length;col++){
                if(shape[row][col]==1){
                    int blockX = block.getX() + col;
                    int blockY = block.getY() + row;
                    if(blockY>=0) {
                        board[blockY][blockX] = 1;
                        boardColor[blockY][blockX]=color;
                    }
                }
            }
        }
    }

    private boolean isLineFull(int row){
        for(int col=0;col<WIDTH;col++){
            if(board[row][col]==0)
                return false;
        }
        return true;
    }

    private void clearLine(int targetRow){
        for(int row=targetRow;row>0;row--)
            for(int col=0;col<WIDTH;col++) {
                board[row][col] = board[row - 1][col];
                boardColor[row][col]=boardColor[row-1][col];
            }
        for(int col=0;col<WIDTH;col++) {
            board[0][col] = 0;
            boardColor[0][col]=null;
        }
    }

    private void checkLine(){
        for(int row=HEIGHT-1;row>=0;row--){
            if(isLineFull(row)){
                clearLine(row);
                row++;
                score+=100;
            }
        }
    }
}
