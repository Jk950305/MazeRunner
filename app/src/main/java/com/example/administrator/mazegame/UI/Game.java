package com.example.administrator.mazegame.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mazegame.Model.CellLocation;
import com.example.administrator.mazegame.Model.CellState;
import com.example.administrator.mazegame.Model.MazeGame;
import com.example.administrator.mazegame.Model.MoveDirection;
import com.example.administrator.mazegame.R;

public class Game extends AppCompatActivity {
    private MazeGame game = new MazeGame(100000);
    private int boardRow = game.getBoardRow();
    private int boardCol = game.getBoardCol();
    private ImageView imageTable[][];
    private boolean gameEnd;
    private int bestScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        imageTable = new ImageView[boardRow][boardCol];
        setMoveButton(R.id.up_btn);
        setMoveButton(R.id.down_btn);
        setMoveButton(R.id.right_btn);
        setMoveButton(R.id.left_btn);
        getSetting();
        updateStat();
        createTable();
    }

    private boolean doWonOrLost() {
        if(game.hasUserLost()){
            Toast.makeText(Game.this, "GAME OVER", Toast.LENGTH_SHORT).show();
            gameEnd = true;
            updateUI();
            setBestScore();
            gameEnd();
            return true;
        }
        return false;
    }

    private void gameEnd() {
        FragmentManager manager = getSupportFragmentManager();
        MessageFragment dialog = new MessageFragment();
        dialog.show(manager, "winning dialog");
        dialog.setCancelable(false);
    }


    static public Intent makeIntent(Context context){
        return (new Intent(context, Game.class));
    }

    private void setMoveButton(final int btnId) {
        Button btn = (Button) findViewById(btnId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveDirection move = MoveDirection.MOVE_NONE;
                if(gameEnd){
                    return;
                }
                switch(btnId){
                    case R.id.up_btn:
                        move = MoveDirection.MOVE_UP;
                        break;
                    case R.id.down_btn:
                        move = MoveDirection.MOVE_DOWN;
                        break;
                    case R.id.right_btn:
                        move = MoveDirection.MOVE_RIGHT;
                        break;
                    case R.id.left_btn:
                        move = MoveDirection.MOVE_LEFT;
                        break;
                }
                if(game.isValidPlayerMove(move)){
                    game.recordPlayerMove(move);
                    if(game.isRevealed()){
                        game.incrementCatProduceDuration();
                    }
                    if(doWonOrLost()){
                        return;
                    }
                    game.doCatMoves();
                    if(doWonOrLost()){
                        return;
                    }
                }
                updateUI();
            }
        });
    }

    private void createTable(){
        TableLayout tLayout = (TableLayout) findViewById(R.id.tableLayout);
        for(int i = 0 ; i < boardCol ; i++){
            TableRow tRow = new TableRow(this);
            tRow.setLayoutParams(new TableLayout.LayoutParams( TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            tLayout.addView(tRow);
            for(int j = 0 ; j < boardRow ; j++){
                final int row = i;
                final int col = j;
                ImageView img = new ImageView(this);
                img.setLayoutParams( new TableRow.LayoutParams( TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f) );
                img.setScaleType(ImageView.ScaleType.FIT_XY);
                tRow.addView(img);
                imageTable[j][i] = img;
            }
        }
        updateboard();
    }

    private void updateUI(){
        updateboard();
        updateStat();
    }
    private void updateboard(){
        for(int i = 0 ; i < boardCol ; i++){
            for(int j = 0 ; j < boardRow ; j++){
                ImageView img = imageTable[j][i];
                CellLocation cell = new CellLocation(j,i);
                img.setImageResource(getImageIdForCell(cell));
            }
        }
    }

    private void updateStat(){

        TextView score = (TextView) findViewById(R.id.score_tv);
        score.setText(""+game.getScore());

        TextView level = (TextView) findViewById(R.id.level_count_tv);
        level.setText(""+game.getNumCats());

        TextView nextCat = (TextView) findViewById(R.id.nxtCat_tv);
        if(!game.isRevealed()){
            nextCat.setText(""+game.getCountUntilNextCat());
        }else{
            nextCat.setText("Map Revealed");
        }

        TextView bstScore = (TextView) findViewById(R.id.bestScore_tv);
        if(bestScore < game.getScore()){
            bestScore = game.getScore();
        }
        bstScore.setText(""+bestScore);
    }


    private int getImageIdForCell(CellLocation cell){
        CellState state = game.getCellState(cell);
        if(game.isMouseAtLocation(cell) && game.isCatAtLocation(cell)){
            return R.drawable.eaten;
        } else if(game.isMouseAtLocation(cell)){
            return R.drawable.mouse;
        } else if(game.isCatAtLocation(cell)){
            return R.drawable.cat;
        } else if(game.isCheeseAtLocation(cell)){
            return R.drawable.cheese;
        } else if(state.isHidden()){
            return R.drawable.unrevealed;
        } else if(state.isWall()){
            return R.drawable.wall;
        } else{
            return R.drawable.space;
        }
    }

    public void getSetting(){
        SharedPreferences setting = getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE);
        bestScore = setting.getInt(MainActivity.BEST_SCORE,0);
    }

    public void setBestScore(){
        SharedPreferences setting = getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        int score = game.getScore();
        if(score<bestScore){
            score = bestScore;
        }
        editor.putInt(MainActivity.BEST_SCORE, score);
        editor.apply();
    }



}
