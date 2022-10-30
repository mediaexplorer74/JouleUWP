package SevenZip;

import SevenZip.Compression.LZMA.Decoder;
import SevenZip.Compression.LZMA.Encoder;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.chromium.ui.base.PageTransition;

public class LzmaAlone {

    public static class CommandLine {
        public static final int kBenchmak = 2;
        public static final int kDecode = 1;
        public static final int kEncode = 0;
        public int Algorithm;
        public int Command;
        public int DictionarySize;
        public boolean DictionarySizeIsDefined;
        public boolean Eos;
        public int Fb;
        public boolean FbIsDefined;
        public String InFile;
        public int Lc;
        public int Lp;
        public int MatchFinder;
        public int NumBenchmarkPasses;
        public String OutFile;
        public int Pb;

        public CommandLine() {
            this.Command = -1;
            this.NumBenchmarkPasses = 10;
            this.DictionarySize = PageTransition.BLOCKED;
            this.DictionarySizeIsDefined = false;
            this.Lc = 3;
            this.Lp = 0;
            this.Pb = kBenchmak;
            this.Fb = TransportMediator.FLAG_KEY_MEDIA_NEXT;
            this.FbIsDefined = false;
            this.Eos = false;
            this.Algorithm = kBenchmak;
            this.MatchFinder = kDecode;
        }

        boolean ParseSwitch(String s) {
            if (s.startsWith("d")) {
                this.DictionarySize = kDecode << Integer.parseInt(s.substring(kDecode));
                this.DictionarySizeIsDefined = true;
            } else if (s.startsWith("fb")) {
                this.Fb = Integer.parseInt(s.substring(kBenchmak));
                this.FbIsDefined = true;
            } else if (s.startsWith("a")) {
                this.Algorithm = Integer.parseInt(s.substring(kDecode));
            } else if (s.startsWith("lc")) {
                this.Lc = Integer.parseInt(s.substring(kBenchmak));
            } else if (s.startsWith("lp")) {
                this.Lp = Integer.parseInt(s.substring(kBenchmak));
            } else if (s.startsWith("pb")) {
                this.Pb = Integer.parseInt(s.substring(kBenchmak));
            } else if (s.startsWith("eos")) {
                this.Eos = true;
            } else if (!s.startsWith("mf")) {
                return false;
            } else {
                String mfs = s.substring(kBenchmak);
                if (mfs.equals("bt2")) {
                    this.MatchFinder = 0;
                } else if (mfs.equals("bt4")) {
                    this.MatchFinder = kDecode;
                } else if (!mfs.equals("bt4b")) {
                    return false;
                } else {
                    this.MatchFinder = kBenchmak;
                }
            }
            return true;
        }

        public boolean Parse(String[] args) throws Exception {
            int pos = 0;
            boolean switchMode = true;
            for (int i = 0; i < args.length; i += kDecode) {
                String s = args[i];
                if (s.length() == 0) {
                    return false;
                }
                if (switchMode) {
                    if (s.compareTo("--") == 0) {
                        switchMode = false;
                    } else if (s.charAt(0) == '-') {
                        String sw = s.substring(kDecode).toLowerCase();
                        if (sw.length() == 0) {
                            return false;
                        }
                        try {
                            if (!ParseSwitch(sw)) {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                }
                if (pos == 0) {
                    if (s.equalsIgnoreCase("e")) {
                        this.Command = 0;
                    } else if (s.equalsIgnoreCase("d")) {
                        this.Command = kDecode;
                    } else if (!s.equalsIgnoreCase("b")) {
                        return false;
                    } else {
                        this.Command = kBenchmak;
                    }
                } else if (pos == kDecode) {
                    if (this.Command == kBenchmak) {
                        try {
                            this.NumBenchmarkPasses = Integer.parseInt(s);
                            if (this.NumBenchmarkPasses < kDecode) {
                                return false;
                            }
                        } catch (NumberFormatException e2) {
                            return false;
                        }
                    }
                    this.InFile = s;
                } else if (pos != kBenchmak) {
                    return false;
                } else {
                    this.OutFile = s;
                }
                pos += kDecode;
            }
            return true;
        }
    }

    static void PrintHelp() {
        System.out.println("\nUsage:  LZMA <e|d> [<switches>...] inputFile outputFile\n  e: encode file\n  d: decode file\n  b: Benchmark\n<Switches>\n  -d{N}:  set dictionary - [0,28], default: 23 (8MB)\n  -fb{N}: set number of fast bytes - [5, 273], default: 128\n  -lc{N}: set number of literal context bits - [0, 8], default: 3\n  -lp{N}: set number of literal pos bits - [0, 4], default: 0\n  -pb{N}: set number of pos bits - [0, 4], default: 2\n  -mf{MF_ID}: set Match Finder: [bt2, bt4], default: bt4\n  -eos:   write End Of Stream marker\n");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("\nLZMA (Java) 4.61  2008-11-23\n");
        if (args.length < 1) {
            PrintHelp();
            return;
        }
        CommandLine params = new CommandLine();
        if (!params.Parse(args)) {
            System.out.println("\nIncorrect command");
        } else if (params.Command == 2) {
            int dictionary = AccessibilityNodeInfoCompat.ACTION_SET_TEXT;
            if (params.DictionarySizeIsDefined) {
                dictionary = params.DictionarySize;
            }
            if (params.MatchFinder > 1) {
                throw new Exception("Unsupported match finder");
            }
            LzmaBench.LzmaBenchmark(params.NumBenchmarkPasses, dictionary);
        } else if (params.Command == 0 || params.Command == 1) {
            File file = new File(params.InFile);
            file = new File(params.OutFile);
            BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
            boolean eos = false;
            if (params.Eos) {
                eos = true;
            }
            int i;
            if (params.Command == 0) {
                Encoder encoder = new Encoder();
                if (!encoder.SetAlgorithm(params.Algorithm)) {
                    throw new Exception("Incorrect compression mode");
                } else if (!encoder.SetDictionarySize(params.DictionarySize)) {
                    throw new Exception("Incorrect dictionary size");
                } else if (!encoder.SetNumFastBytes(params.Fb)) {
                    throw new Exception("Incorrect -fb value");
                } else if (!encoder.SetMatchFinder(params.MatchFinder)) {
                    throw new Exception("Incorrect -mf value");
                } else if (encoder.SetLcLpPb(params.Lc, params.Lp, params.Pb)) {
                    long fileSize;
                    encoder.SetEndMarkerMode(eos);
                    encoder.WriteCoderProperties(outStream);
                    if (eos) {
                        fileSize = -1;
                    } else {
                        fileSize = file.length();
                    }
                    for (i = 0; i < 8; i++) {
                        outStream.write(((int) (fileSize >>> (i * 8))) & PageTransition.CORE_MASK);
                    }
                    encoder.Code(inStream, outStream, -1, -1, null);
                } else {
                    throw new Exception("Incorrect -lc or -lp or -pb value");
                }
            }
            byte[] properties = new byte[5];
            if (inStream.read(properties, 0, 5) != 5) {
                throw new Exception("input .lzma file is too short");
            }
            Decoder decoder = new Decoder();
            if (decoder.SetDecoderProperties(properties)) {
                long outSize = 0;
                for (i = 0; i < 8; i++) {
                    int v = inStream.read();
                    if (v < 0) {
                        throw new Exception("Can't read stream size");
                    }
                    outSize |= ((long) v) << (i * 8);
                }
                if (!decoder.Code(inStream, outStream, outSize)) {
                    throw new Exception("Error in data stream");
                }
            }
            throw new Exception("Incorrect stream properties");
            outStream.flush();
            outStream.close();
            inStream.close();
        } else {
            throw new Exception("Incorrect command");
        }
    }
}
