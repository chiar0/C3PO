package reader;

import java.io.InputStream;
import java.util.Vector;

public class MiniReader {
   String source;
   int mark = 0;

   public MiniReader(InputStream is) {
      byte[] b = new byte['耀'];
      this.source = "";

      try {
         while(true) {
            int read = is.read(b);
            if (read <= 0) {
               break;
            }

            String part = new String(b);
            this.source = this.source + part;
         }
      } catch (Exception var5) {
      }

   }

   public MiniReader(String s) {
      this.source = s;
   }

   public String getNextToken(boolean moveMark) {
      int mymark;
      for(mymark = this.mark; mymark < this.source.length() && (this.source.charAt(mymark) == ' ' || this.source.charAt(mymark) == '\n' || this.source.charAt(mymark) == '\r' || this.source.charAt(mymark) == '\t'); ++mymark) {
      }

      if (mymark >= this.source.length()) {
         if (moveMark) {
            this.mark = mymark;
         }

         return "";
      } else {
         int i1 = this.source.indexOf(60, mymark + 1);
         int i2 = this.source.indexOf(62, mymark + 1);
         String result;
         if (this.source.charAt(mymark) == '<') {
            if (i2 < 0) {
               if (moveMark) {
                  this.mark = mymark;
               }

               return "";
            } else {
               result = this.source.substring(mymark, i2 + 1);
               if (moveMark) {
                  this.mark = i2 + 1;
               }

               return result;
            }
         } else if (i1 < 0) {
            if (moveMark) {
               this.mark = mymark;
            }

            return "";
         } else {
            result = this.source.substring(mymark, i1);
            result = result.trim();
            if (moveMark) {
               this.mark = i1;
            }

            return result;
         }
      }
   }

   public String viewNextToken() {
      return this.getNextToken(false);
   }

   public String getNextToken() {
      return this.getNextToken(true);
   }

   public void resetMark() {
      this.mark = 0;
   }

   public MiniReader.ReaderTag parse() {
      Vector<MiniReader.ReaderTag> tagStack = new Vector();
      String input = "";

      do {
         input = this.getNextToken();
         if (input.startsWith("<")) {
            String input2 = input.substring(1, input.length() - 1);
            MiniReader.ReaderTag rt;
            if (input2.startsWith("/")) {
               rt = (MiniReader.ReaderTag)tagStack.lastElement();
               String orig = input2.substring(1, input2.length());
               if (!orig.equals(rt.tag)) {
                  return null;
               }

               tagStack.remove(rt);
               if (tagStack.size() < 1) {
                  return rt;
               }

               ((MiniReader.ReaderTag)tagStack.lastElement()).subtags.add(rt);
            } else {
               rt = new MiniReader.ReaderTag();
               rt.tag = input2;
               tagStack.add(rt);
            }
         } else {
            if (tagStack.size() < 1) {
               return null;
            }

            ((MiniReader.ReaderTag)tagStack.lastElement()).value = input;
         }
      } while(!input.equals(""));

      return null;
   }

   public class ReaderTag {
      public String tag = "";
      public String value = "";
      public Vector<MiniReader.ReaderTag> subtags = new Vector();
   }
}
