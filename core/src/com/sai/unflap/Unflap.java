package com.sai.unflap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Unflap extends ApplicationAdapter {

	SpriteBatch batch;
	Texture background;
	Texture [] birds;
	Texture toptube,bottomtube,gameover;

	float gap =400;
	int flag=0;
	float birdY = 0;
	float velocity=0;
	float gravity = -2;   //-2 for reverse gravity
	float maxTubeOffset;
	int gameState = 0;
	Random rangen;
	float tubeVelocity = 4;
	int num=4;
	float[] tubeX = new float[num];
	float distance;
	float[] tubeOffset = new float[num];

	int score = 0;
	int scoreTube = 0;
	int collNum = 0;
	int high_score = 0;
	int velocity_flag = 0;

	BitmapFont font,font2;

	Circle birdCircle;

	//ShapeRenderer shapeRenderer;

	Rectangle[] topRec;
	Rectangle[] botRec;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		gameover = new Texture("game_over.png");

		//shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(6);

		font2 = new BitmapFont();
		font2.setColor(Color.WHITE);
		font2.getData().setScale(8);

		background = new Texture("background2.png");
		birds = new Texture[2];
		birds[0] = new Texture("red.png");
		birds[1] = new Texture("red.png");

		toptube = new Texture("toptube.png");
		bottomtube = new Texture("bottomtube.png");



		maxTubeOffset = Gdx.graphics.getHeight()/-gap/2-100;
		rangen = new Random();
		distance = Gdx.graphics.getWidth()*3/4;

		topRec = new Rectangle[num];
		botRec = new Rectangle[num];

		reset();

	}

	public void reset(){
		birdY=(Gdx.graphics.getHeight()/2)-birds[flag].getHeight()/2;

		for(int i=0;i<num;i++){


			tubeOffset[i] = (rangen.nextFloat()-0.5f) * (Gdx.graphics.getHeight()/2-gap-200);

			tubeX[i] = Gdx.graphics.getWidth()/2-toptube.getWidth()/2+Gdx.graphics.getWidth()+i*distance;

			topRec[i] = new Rectangle();
			botRec[i] = new Rectangle();

		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState==1) {

			if(velocity_flag==1){
				gravity = -0.5f;
			}else{
				gravity = -2;
			}

			if(tubeX[scoreTube]<Gdx.graphics.getWidth()/2){
				score++;

				if(scoreTube<num-1){
					scoreTube++;
				}else{
					scoreTube=0;
				}
			}

			if(Gdx.input.justTouched()){

				velocity = +30;  //+30 for moving bird down when tapped
				if(velocity_flag==1){
					velocity = 10;
				}

			}

			for(int i=0;i<num;i++) {

				if(tubeX[i] < -toptube.getWidth()){
					tubeX[i] +=num * distance;
					tubeOffset[i] = (rangen.nextFloat()-0.5f) * (Gdx.graphics.getHeight()/2-gap-200);
				}else{
				tubeX[i] -= tubeVelocity;
				}

				batch.draw(toptube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomtube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOffset[i]);

				topRec[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],toptube.getWidth(),toptube.getHeight());
				botRec[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOffset[i],bottomtube.getWidth(),bottomtube.getHeight());
			}
			if(birdY>0 && birdY<Gdx.graphics.getHeight()) {

				velocity += gravity;
				birdY -= velocity;
			}else{
				gameState = 2;
			}

		}else if(gameState == 0){
			if(Gdx.input.justTouched()){

				gameState = 1;

			}
		}else if(gameState==2){
			batch.draw(gameover,Gdx.graphics.getWidth()/2-gameover.getWidth()/2,Gdx.graphics.getHeight()/2-gameover.getHeight()/2);

			if(Gdx.input.justTouched()){

				Vector2 tmp=new Vector2(Gdx.input.getX(),Gdx.input.getY());
				Rectangle textureBounds=new Rectangle(Gdx.graphics.getWidth()/2-gameover.getWidth()/2,Gdx.graphics.getHeight()/2-gameover.getHeight()/2,gameover.getWidth(),gameover.getHeight());
				if(textureBounds.contains(tmp.x,tmp.y))
				{
					gameState = 4;

					reset();
					score=0;
					scoreTube=0;
					velocity=0;
				}else {

					gameState = 3;

					reset();
					score = 0;
					scoreTube = 0;
					velocity = 0;
				}

			}

		}else if(gameState==3){
			velocity_flag=0;
			if(Gdx.input.justTouched()){
				gameState=1;
			}
		}else if(gameState == 4){
			velocity_flag=1;
			if(Gdx.input.justTouched()){
				gameState=1;
			}
		}

		if (flag == 0) {
			flag = 1;
		} else {
			flag = 0;
		}

		batch.draw(birds[flag], (Gdx.graphics.getWidth() / 2) - birds[flag].getWidth() / 2, birdY);

		font.draw(batch, String.valueOf(score),100,200);

		if(high_score<score){
			high_score = score;
		}
		font2.draw(batch,String.valueOf(high_score),100,1700);

		birdCircle.set(Gdx.graphics.getWidth()/2,birdY + birds[flag].getHeight()/2,birds[flag].getHeight()/2);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

		for(int i=0;i<num;i++){

			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],toptube.getWidth(),toptube.getHeight());
			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOffset[i],bottomtube.getWidth(),bottomtube.getHeight());

			if(Intersector.overlaps(birdCircle,topRec[i]) || Intersector.overlaps(birdCircle,botRec[i])){

				//collNum++;

				//font2.draw(batch, String.valueOf(collNum),500,200);

				gameState = 2;

			}

		}

		//font2.draw(batch, String.valueOf(collNum),500,200);

		//shapeRenderer.end();

		batch.end();

	}

}
