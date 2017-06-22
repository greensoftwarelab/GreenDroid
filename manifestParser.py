#!/usr/bin/python

import xml.sax, sys

class ManifestHandler( xml.sax.ContentHandler ):
   def __init__(self, path):
      self.path = path
      self.CurrentData = ""
      self.target = ""
      self.package = ""
      self.launcher = False

   # Call when an element starts
   def startElement(self, tag, attributes):
      self.CurrentData = tag
      if tag == "instrumentation":
         if "android:targetPackage" in attributes:
            self.target = attributes["android:targetPackage"]
      elif tag == "manifest":
         if "package" in attributes:
            self.package = attributes["package"]
      elif tag == "category":
         if attributes["android:name"]:
            if attributes["android:name"] == "android.intent.category.LAUNCHER":
               self.launcher = True

def getLauncher(handlers):
   for h in handlers:
      if h.launcher:
         return h.path, h.package
  
def main(argv):
   lst = []
   for arg in argv:
      path = arg.replace("/AndroidManifest.xml", "")
      # create an XMLReader
      parser = xml.sax.make_parser()
      # turn off namepsaces
      parser.setFeature(xml.sax.handler.feature_namespaces, 0)

      # override the default ContextHandler
      handler = ManifestHandler(path)
      parser.setContentHandler( handler )
      
      parser.parse(arg)

      lst.append(handler)
      lst_cpy = lst
      #print(handler.package)

   p, source, tests, package, testPack="","","","",""
   #count=0
   for h in lst:
      if h.target != "":
         #test project found!!
         tests=h.path
         p=h.target
         lst.remove(h)
         #count+=1
         if p != "":
            for x in lst:
               isSubstring = (x.package in p) and (x.package != "") and (x.target == "")
               if isSubstring:
                  testPack=h.package
                  source=x.path
                  package=x.package
                  break
      if source != "":
         break
   
   if (source == "") & (package == ""):
      source, package = getLauncher(lst_cpy)

   print(source)
   print(tests)
   print(package)
   print(testPack)

if __name__ == "__main__":
   main(sys.argv[1:])
