package Renderer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {
    private int shaderProgramID;
    private boolean beingUsed = false;
    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath){
        this.filepath = filepath;
        try {
            //Opens file
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            //Find the first occurence of #type XXX
            int index = source.indexOf("#type") + 6;
            int endOfLineIndex = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, endOfLineIndex).trim();

            //Find the second occurence of #type XXX
            index = source.indexOf("#type", endOfLineIndex) + 6;
            endOfLineIndex = source.indexOf("\r\n", index) ;
            String secondPattern = source.substring(index, endOfLineIndex).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")){
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token : '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")){
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token : '" + secondPattern + "'");
            }
        } catch(IOException e){
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" +filepath+ "'";

        }
    }

    public void compileAndLink(){
        int vertexID, fragmentID;
        //Compile the vertexShader
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        //Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '"+filepath +"' \n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }
        //End of Vertex Shader Compilation
        //Compilation of Fragment Shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        //Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '"+filepath +"' \n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }
        //End of Fragment Shader Compilation
        //Link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        //check for errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE ){
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '"+filepath +"' \n\tShader Program link failed.");
            System.out.println(glGetShaderInfoLog(shaderProgramID, len));
            assert false : "";
        }
        //END of LINKING

    }

    public void use(){
        if (!beingUsed){
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void detach(){
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec4f){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec4f.x, vec4f.y, vec4f.z, vec4f.w);
    }

    public void uploadVec3f(String varName, Vector3f vec3f){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec3f.x, vec3f.y, vec3f.z);
    }

    public void uploadFloat(String varName, float floatToUpload){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, floatToUpload);
    }

    public void uploadINt(String varName, int intToUpload){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, intToUpload);
    }

    public void uploadTexture(String varName, int slot){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);

    }
}
