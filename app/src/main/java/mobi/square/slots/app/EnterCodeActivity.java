package mobi.square.slots.app;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EnterCodeActivity extends Activity {

    //Pxsmsunlock_handler		handler;
    //Pxsmsunlock_params		params;
    String codeToEnter = "";

    //private static EnterCodeActivity app = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entercode);

        //  params	= new Pxsmsunlock_params( );
        //params.get_extra( this );

        /*** Set up the instance ***/
        //	handler	= new Pxsmsunlock_handler( this, params );

        //Localizar los controles
        final TextView labelName = (TextView) findViewById(R.id.inputCodeHeader);
        labelName.setText("Enter the code received");
        //labelName.setTextColor(Color.WHITE);
        final EditText inputCodeValue = (EditText) findViewById(R.id.inputCodeValue);

        Button btnAccept = (Button) findViewById(R.id.btnAccept);
        btnAccept.setText("ok");
        // app = this;
        /* if(this.getIntent().getExtras()!=null){
                //Bundle bundle = this.getIntent().getExtras();
        		labelName.setText(codeToEnter);
        		
        }*/


        btnAccept.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
	            	
	    	            	
	    	            	/* app.runOnGLThread(new Runnable() {
	        	                 @Override
	        	                 public void run() {
	        	                	 codeToEnter = txtNombre.getText().toString();
	        	                	 String evalStr = "onReceiveCode("+ codeToEnter +")";
	        	                 	Cocos2dxJavascriptJavaBridge.evalString(evalStr);
	        	                 }
	        	             });*/
                Intent data = new Intent();
                Bundle b = new Bundle();
                b.putString("EditText", inputCodeValue.getText().toString());
                data.putExtras(b);
                setResult(Activity.RESULT_OK, data);
                finish();

            }
        });


    }

}
