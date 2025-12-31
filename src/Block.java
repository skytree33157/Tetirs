import java.awt.Color;
public class Block {

    private int[][] curShape;
    private Color curColor;
    private int x, y;

    public static final int [][][] shape={
            {
                    {0,0,0,0},
                    {1,1,1,1},
                    {0,0,0,0},
                    {0,0,0,0}
            },
            {
                    {1,0,0},
                    {1,1,1},
                    {0,0,0}
            },
            {
                    {0,0,1},
                    {1,1,1},
                    {0,0,0}
            },
            {
                    {1,1},
                    {1,1}
            },
            {
                    {0,1,1},
                    {1,1,0},
                    {0,0,0}
            },
            {
                    {1,1,0},
                    {0,1,1},
                    {0,0,0}
            },
            {
                    {0,1,0},
                    {1,1,1},
                    {0,0,0}
            }
    };

    public static final Color[] color={
            Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.red, Color.magenta
    };

    public Block(int idx){
        this.curShape=shape[idx];
        this.curColor=color[idx];
        //블럭 초기 위치
        this.x=4;this.y=0;
    }

    public int[][] getCurShape() {
        return curShape;
    }

    public void setCurShape(int[][] curShape) {
        this.curShape = curShape;
    }

    public Color getCurColor() {
        return curColor;
    }

    public void setCurColor(Color curColor) {
        this.curColor = curColor;
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

    public void moveDown(){
        y++;
        GamePanel.score++;
    }

    public void moveLeft(){
        x--;
    }

    public void moveRight(){
        x++;
    }

    public void rotate(){
        int[][] cur=getCurShape();
        int N=cur.length;
        int tmp[][]=new int[N][N];
        for(int row=0;row<N;row++)
            for(int col=0;col<N;col++)
                tmp[col][N-1-row]=cur[row][col];
        setCurShape(tmp);
    }
}
