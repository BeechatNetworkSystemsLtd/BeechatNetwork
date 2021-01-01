 /*
  * This file is part of BeeChat.
  *
  *     BeeChat is free software: you can redistribute it and/or modify
  *     it under the terms of the GNU General Public License as published by
  *     the Free Software Foundation, either version 3 of the License, or
  *     (at your option) any later version.
  *
  *     BeeChat is distributed in the hope that it will be useful,
  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  *     GNU General Public License for more details.
  *
  *     You should have received a copy of the GNU General Public License
  *     along with BeeChat.  If not, see <https://www.gnu.org/licenses/>.
  */

 package src;

 import javafx.application.Application;
 import javafx.fxml.FXMLLoader;
 import javafx.scene.Parent;
 import javafx.scene.Scene;
 import javafx.stage.Stage;

 import java.io.IOException;

 public class Main extends Application {

     public static void main(String[] args) {
         // TODO: 1/1/21 Parse command line options, set up network, etc
         launch(args);
     }

     @Override
     public void start(Stage primaryStage) throws IOException {
         Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));

         Scene scene = new Scene(root, 300, 275);

         primaryStage.setTitle("FXML Welcome");
         primaryStage.setScene(scene);
         primaryStage.show();
     }
 }
