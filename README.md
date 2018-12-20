# MuPDFDemo-Java
Demo app to show how to use MuPDF Library for Android.

# Introduction: 
MuPDF is a  lightweight PDF and XPS viewer  for Android and iOS. There are many other libraries that provide PDF support but MuPDF is by far the best that we have used.


# Showing PDF: 
With this method you can choose how to show the PDF file. 
```ruby

        private void displayPDF(String path) throws Exception {
        /*Reading file from URI*/
        byte buffer[] = null;
        Uri uri = Uri.parse(path);
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            int len = is.available();
            buffer = new byte[len];
            is.read(buffer, 0, len);
            is.close();
        } catch (OutOfMemoryError e) {
            System.out.println("Out of memory during buffer reading");
        } catch (Exception e) {
            System.out.println("Exception reading from stream: " + e);
            try {
                Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
                if (cursor.moveToFirst()) {
                    String str = cursor.getString(0);
                    if (str == null) {
                    } else {
                        uri = Uri.parse(str);
                    }
                }
            } catch (Exception e2) {
                System.out.println("Exception in Transformer Prime file manager code: " + e2);
            }
        }
        MuPDFCore core = new MuPDFCore(getApplicationContext(), buffer, null);
        MuPDFReaderView mDocView = new MuPDFReaderView(getApplicationContext());
        mDocView.setAdapter(new MuPDFPageAdapter(getApplicationContext(), null, core));
        pdfViewRL.addView(mDocView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        
        /*Use this if you want to open PDF outside app using MuPDF's own Activity*/

        /*Intent intent = new Intent(getApplicationContext(), MuPDFActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(path));
        startActivity(intent);*/
        
    }
    
