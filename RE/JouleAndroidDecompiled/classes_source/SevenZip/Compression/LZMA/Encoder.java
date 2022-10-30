package SevenZip.Compression.LZMA;

import SevenZip.Compression.LZ.BinTree;
import SevenZip.Compression.RangeCoder.BitTreeEncoder;
import SevenZip.ICodeProgress;
import android.support.v4.media.TransportMediator;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.chromium.blink_public.web.WebInputEventModifier;
import org.chromium.blink_public.web.WebTextInputFlags;
import org.chromium.ui.base.PageTransition;

public class Encoder {
    public static final int EMatchFinderTypeBT2 = 0;
    public static final int EMatchFinderTypeBT4 = 1;
    static byte[] g_FastPos = null;
    static final int kDefaultDictionaryLogSize = 22;
    static final int kIfinityPrice = 268435455;
    static final int kNumFastBytesDefault = 32;
    public static final int kNumLenSpecSymbols = 16;
    static final int kNumOpts = 4096;
    public static final int kPropSize = 5;
    int _additionalOffset;
    int _alignPriceCount;
    int[] _alignPrices;
    int _dictionarySize;
    int _dictionarySizePrev;
    int _distTableSize;
    int[] _distancesPrices;
    boolean _finished;
    InputStream _inStream;
    short[] _isMatch;
    short[] _isRep;
    short[] _isRep0Long;
    short[] _isRepG0;
    short[] _isRepG1;
    short[] _isRepG2;
    LenPriceTableEncoder _lenEncoder;
    LiteralEncoder _literalEncoder;
    int _longestMatchLength;
    boolean _longestMatchWasFound;
    int[] _matchDistances;
    BinTree _matchFinder;
    int _matchFinderType;
    int _matchPriceCount;
    boolean _needReleaseMFStream;
    int _numDistancePairs;
    int _numFastBytes;
    int _numFastBytesPrev;
    int _numLiteralContextBits;
    int _numLiteralPosStateBits;
    Optimal[] _optimum;
    int _optimumCurrentIndex;
    int _optimumEndIndex;
    BitTreeEncoder _posAlignEncoder;
    short[] _posEncoders;
    BitTreeEncoder[] _posSlotEncoder;
    int[] _posSlotPrices;
    int _posStateBits;
    int _posStateMask;
    byte _previousByte;
    SevenZip.Compression.RangeCoder.Encoder _rangeEncoder;
    int[] _repDistances;
    LenPriceTableEncoder _repMatchLenEncoder;
    int _state;
    boolean _writeEndMark;
    int backRes;
    boolean[] finished;
    long nowPos64;
    long[] processedInSize;
    long[] processedOutSize;
    byte[] properties;
    int[] repLens;
    int[] reps;
    int[] tempPrices;

    class LenEncoder {
        short[] _choice;
        BitTreeEncoder _highCoder;
        BitTreeEncoder[] _lowCoder;
        BitTreeEncoder[] _midCoder;

        public LenEncoder() {
            this._choice = new short[2];
            this._lowCoder = new BitTreeEncoder[Encoder.kNumLenSpecSymbols];
            this._midCoder = new BitTreeEncoder[Encoder.kNumLenSpecSymbols];
            this._highCoder = new BitTreeEncoder(8);
            for (int posState = Encoder.EMatchFinderTypeBT2; posState < Encoder.kNumLenSpecSymbols; posState += Encoder.EMatchFinderTypeBT4) {
                this._lowCoder[posState] = new BitTreeEncoder(3);
                this._midCoder[posState] = new BitTreeEncoder(3);
            }
        }

        public void Init(int numPosStates) {
            SevenZip.Compression.RangeCoder.Encoder.InitBitModels(this._choice);
            for (int posState = Encoder.EMatchFinderTypeBT2; posState < numPosStates; posState += Encoder.EMatchFinderTypeBT4) {
                this._lowCoder[posState].Init();
                this._midCoder[posState].Init();
            }
            this._highCoder.Init();
        }

        public void Encode(SevenZip.Compression.RangeCoder.Encoder rangeEncoder, int symbol, int posState) throws IOException {
            if (symbol < 8) {
                rangeEncoder.Encode(this._choice, Encoder.EMatchFinderTypeBT2, Encoder.EMatchFinderTypeBT2);
                this._lowCoder[posState].Encode(rangeEncoder, symbol);
                return;
            }
            symbol -= 8;
            rangeEncoder.Encode(this._choice, Encoder.EMatchFinderTypeBT2, Encoder.EMatchFinderTypeBT4);
            if (symbol < 8) {
                rangeEncoder.Encode(this._choice, Encoder.EMatchFinderTypeBT4, Encoder.EMatchFinderTypeBT2);
                this._midCoder[posState].Encode(rangeEncoder, symbol);
                return;
            }
            rangeEncoder.Encode(this._choice, Encoder.EMatchFinderTypeBT4, Encoder.EMatchFinderTypeBT4);
            this._highCoder.Encode(rangeEncoder, symbol - 8);
        }

        public void SetPrices(int posState, int numSymbols, int[] prices, int st) {
            int a0 = SevenZip.Compression.RangeCoder.Encoder.GetPrice0(this._choice[Encoder.EMatchFinderTypeBT2]);
            int a1 = SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._choice[Encoder.EMatchFinderTypeBT2]);
            int b0 = a1 + SevenZip.Compression.RangeCoder.Encoder.GetPrice0(this._choice[Encoder.EMatchFinderTypeBT4]);
            int b1 = a1 + SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._choice[Encoder.EMatchFinderTypeBT4]);
            int i = Encoder.EMatchFinderTypeBT2;
            while (i < 8) {
                if (i < numSymbols) {
                    prices[st + i] = this._lowCoder[posState].GetPrice(i) + a0;
                    i += Encoder.EMatchFinderTypeBT4;
                } else {
                    return;
                }
            }
            while (i < Encoder.kNumLenSpecSymbols) {
                if (i < numSymbols) {
                    prices[st + i] = this._midCoder[posState].GetPrice(i - 8) + b0;
                    i += Encoder.EMatchFinderTypeBT4;
                } else {
                    return;
                }
            }
            while (i < numSymbols) {
                prices[st + i] = this._highCoder.GetPrice((i - 8) - 8) + b1;
                i += Encoder.EMatchFinderTypeBT4;
            }
        }
    }

    class LiteralEncoder {
        Encoder2[] m_Coders;
        int m_NumPosBits;
        int m_NumPrevBits;
        int m_PosMask;

        class Encoder2 {
            short[] m_Encoders;

            Encoder2() {
                this.m_Encoders = new short[768];
            }

            public void Init() {
                SevenZip.Compression.RangeCoder.Encoder.InitBitModels(this.m_Encoders);
            }

            public void Encode(SevenZip.Compression.RangeCoder.Encoder rangeEncoder, byte symbol) throws IOException {
                int context = Encoder.EMatchFinderTypeBT4;
                for (int i = 7; i >= 0; i--) {
                    int bit = (symbol >> i) & Encoder.EMatchFinderTypeBT4;
                    rangeEncoder.Encode(this.m_Encoders, context, bit);
                    context = (context << Encoder.EMatchFinderTypeBT4) | bit;
                }
            }

            public void EncodeMatched(SevenZip.Compression.RangeCoder.Encoder rangeEncoder, byte matchByte, byte symbol) throws IOException {
                int context = Encoder.EMatchFinderTypeBT4;
                boolean same = true;
                for (int i = 7; i >= 0; i--) {
                    int bit = (symbol >> i) & Encoder.EMatchFinderTypeBT4;
                    int state = context;
                    if (same) {
                        int matchBit = (matchByte >> i) & Encoder.EMatchFinderTypeBT4;
                        state += (matchBit + Encoder.EMatchFinderTypeBT4) << 8;
                        same = matchBit == bit;
                    }
                    rangeEncoder.Encode(this.m_Encoders, state, bit);
                    context = (context << Encoder.EMatchFinderTypeBT4) | bit;
                }
            }

            public int GetPrice(boolean matchMode, byte matchByte, byte symbol) {
                int bit;
                int price = Encoder.EMatchFinderTypeBT2;
                int context = Encoder.EMatchFinderTypeBT4;
                int i = 7;
                if (matchMode) {
                    while (i >= 0) {
                        int matchBit = (matchByte >> i) & Encoder.EMatchFinderTypeBT4;
                        bit = (symbol >> i) & Encoder.EMatchFinderTypeBT4;
                        price += SevenZip.Compression.RangeCoder.Encoder.GetPrice(this.m_Encoders[((matchBit + Encoder.EMatchFinderTypeBT4) << 8) + context], bit);
                        context = (context << Encoder.EMatchFinderTypeBT4) | bit;
                        if (matchBit != bit) {
                            i--;
                            break;
                        }
                        i--;
                    }
                }
                while (i >= 0) {
                    bit = (symbol >> i) & Encoder.EMatchFinderTypeBT4;
                    price += SevenZip.Compression.RangeCoder.Encoder.GetPrice(this.m_Encoders[context], bit);
                    context = (context << Encoder.EMatchFinderTypeBT4) | bit;
                    i--;
                }
                return price;
            }
        }

        LiteralEncoder() {
        }

        public void Create(int numPosBits, int numPrevBits) {
            if (this.m_Coders == null || this.m_NumPrevBits != numPrevBits || this.m_NumPosBits != numPosBits) {
                this.m_NumPosBits = numPosBits;
                this.m_PosMask = (Encoder.EMatchFinderTypeBT4 << numPosBits) - 1;
                this.m_NumPrevBits = numPrevBits;
                int numStates = Encoder.EMatchFinderTypeBT4 << (this.m_NumPrevBits + this.m_NumPosBits);
                this.m_Coders = new Encoder2[numStates];
                for (int i = Encoder.EMatchFinderTypeBT2; i < numStates; i += Encoder.EMatchFinderTypeBT4) {
                    this.m_Coders[i] = new Encoder2();
                }
            }
        }

        public void Init() {
            int numStates = Encoder.EMatchFinderTypeBT4 << (this.m_NumPrevBits + this.m_NumPosBits);
            for (int i = Encoder.EMatchFinderTypeBT2; i < numStates; i += Encoder.EMatchFinderTypeBT4) {
                this.m_Coders[i].Init();
            }
        }

        public Encoder2 GetSubCoder(int pos, byte prevByte) {
            return this.m_Coders[((this.m_PosMask & pos) << this.m_NumPrevBits) + ((prevByte & PageTransition.CORE_MASK) >>> (8 - this.m_NumPrevBits))];
        }
    }

    class Optimal {
        public int BackPrev;
        public int BackPrev2;
        public int Backs0;
        public int Backs1;
        public int Backs2;
        public int Backs3;
        public int PosPrev;
        public int PosPrev2;
        public boolean Prev1IsChar;
        public boolean Prev2;
        public int Price;
        public int State;

        Optimal() {
        }

        public void MakeAsChar() {
            this.BackPrev = -1;
            this.Prev1IsChar = false;
        }

        public void MakeAsShortRep() {
            this.BackPrev = Encoder.EMatchFinderTypeBT2;
            this.Prev1IsChar = false;
        }

        public boolean IsShortRep() {
            return this.BackPrev == 0;
        }
    }

    class LenPriceTableEncoder extends LenEncoder {
        int[] _counters;
        int[] _prices;
        int _tableSize;

        LenPriceTableEncoder() {
            super();
            this._prices = new int[4352];
            this._counters = new int[Encoder.kNumLenSpecSymbols];
        }

        public void SetTableSize(int tableSize) {
            this._tableSize = tableSize;
        }

        public int GetPrice(int symbol, int posState) {
            return this._prices[(posState * Base.kNumLenSymbols) + symbol];
        }

        void UpdateTable(int posState) {
            SetPrices(posState, this._tableSize, this._prices, posState * Base.kNumLenSymbols);
            this._counters[posState] = this._tableSize;
        }

        public void UpdateTables(int numPosStates) {
            for (int posState = Encoder.EMatchFinderTypeBT2; posState < numPosStates; posState += Encoder.EMatchFinderTypeBT4) {
                UpdateTable(posState);
            }
        }

        public void Encode(SevenZip.Compression.RangeCoder.Encoder rangeEncoder, int symbol, int posState) throws IOException {
            super.Encode(rangeEncoder, symbol, posState);
            int[] iArr = this._counters;
            int i = iArr[posState] - 1;
            iArr[posState] = i;
            if (i == 0) {
                UpdateTable(posState);
            }
        }
    }

    static {
        g_FastPos = new byte[WebInputEventModifier.IsLeft];
        int c = 2;
        g_FastPos[EMatchFinderTypeBT2] = (byte) 0;
        g_FastPos[EMatchFinderTypeBT4] = (byte) 1;
        for (int slotFast = 2; slotFast < kDefaultDictionaryLogSize; slotFast += EMatchFinderTypeBT4) {
            int k = EMatchFinderTypeBT4 << ((slotFast >> EMatchFinderTypeBT4) - 1);
            int j = EMatchFinderTypeBT2;
            while (j < k) {
                g_FastPos[c] = (byte) slotFast;
                j += EMatchFinderTypeBT4;
                c += EMatchFinderTypeBT4;
            }
        }
    }

    static int GetPosSlot(int pos) {
        if (pos < WebInputEventModifier.IsLeft) {
            return g_FastPos[pos];
        }
        if (pos < AccessibilityNodeInfoCompat.ACTION_SET_TEXT) {
            return g_FastPos[pos >> 10] + 20;
        }
        return g_FastPos[pos >> 20] + 40;
    }

    static int GetPosSlot2(int pos) {
        if (pos < AccessibilityNodeInfoCompat.ACTION_SET_SELECTION) {
            return g_FastPos[pos >> 6] + 12;
        }
        if (pos < PageTransition.FROM_API) {
            return g_FastPos[pos >> kNumLenSpecSymbols] + kNumFastBytesDefault;
        }
        return g_FastPos[pos >> 26] + 52;
    }

    void BaseInit() {
        this._state = Base.StateInit();
        this._previousByte = (byte) 0;
        for (int i = EMatchFinderTypeBT2; i < 4; i += EMatchFinderTypeBT4) {
            this._repDistances[i] = EMatchFinderTypeBT2;
        }
    }

    void Create() {
        if (this._matchFinder == null) {
            BinTree bt = new BinTree();
            int numHashBytes = 4;
            if (this._matchFinderType == 0) {
                numHashBytes = 2;
            }
            bt.SetType(numHashBytes);
            this._matchFinder = bt;
        }
        this._literalEncoder.Create(this._numLiteralPosStateBits, this._numLiteralContextBits);
        if (this._dictionarySize != this._dictionarySizePrev || this._numFastBytesPrev != this._numFastBytes) {
            this._matchFinder.Create(this._dictionarySize, kNumOpts, this._numFastBytes, 274);
            this._dictionarySizePrev = this._dictionarySize;
            this._numFastBytesPrev = this._numFastBytes;
        }
    }

    public Encoder() {
        int i;
        this._state = Base.StateInit();
        this._repDistances = new int[4];
        this._optimum = new Optimal[kNumOpts];
        this._matchFinder = null;
        this._rangeEncoder = new SevenZip.Compression.RangeCoder.Encoder();
        this._isMatch = new short[192];
        this._isRep = new short[12];
        this._isRepG0 = new short[12];
        this._isRepG1 = new short[12];
        this._isRepG2 = new short[12];
        this._isRep0Long = new short[192];
        this._posSlotEncoder = new BitTreeEncoder[4];
        this._posEncoders = new short[114];
        this._posAlignEncoder = new BitTreeEncoder(4);
        this._lenEncoder = new LenPriceTableEncoder();
        this._repMatchLenEncoder = new LenPriceTableEncoder();
        this._literalEncoder = new LiteralEncoder();
        this._matchDistances = new int[548];
        this._numFastBytes = kNumFastBytesDefault;
        this._posSlotPrices = new int[WebTextInputFlags.AutocapitalizeWords];
        this._distancesPrices = new int[WebTextInputFlags.AutocapitalizeSentences];
        this._alignPrices = new int[kNumLenSpecSymbols];
        this._distTableSize = 44;
        this._posStateBits = 2;
        this._posStateMask = 3;
        this._numLiteralPosStateBits = EMatchFinderTypeBT2;
        this._numLiteralContextBits = 3;
        this._dictionarySize = 4194304;
        this._dictionarySizePrev = -1;
        this._numFastBytesPrev = -1;
        this._matchFinderType = EMatchFinderTypeBT4;
        this._writeEndMark = false;
        this._needReleaseMFStream = false;
        this.reps = new int[4];
        this.repLens = new int[4];
        this.processedInSize = new long[EMatchFinderTypeBT4];
        this.processedOutSize = new long[EMatchFinderTypeBT4];
        this.finished = new boolean[EMatchFinderTypeBT4];
        this.properties = new byte[kPropSize];
        this.tempPrices = new int[TransportMediator.FLAG_KEY_MEDIA_NEXT];
        for (i = EMatchFinderTypeBT2; i < kNumOpts; i += EMatchFinderTypeBT4) {
            this._optimum[i] = new Optimal();
        }
        for (i = EMatchFinderTypeBT2; i < 4; i += EMatchFinderTypeBT4) {
            this._posSlotEncoder[i] = new BitTreeEncoder(6);
        }
    }

    void SetWriteEndMarkerMode(boolean writeEndMarker) {
        this._writeEndMark = writeEndMarker;
    }

    void Init() {
        BaseInit();
        this._rangeEncoder.Init();
        SevenZip.Compression.RangeCoder.Encoder.InitBitModels(this._isMatch);
        SevenZip.Compression.RangeCoder.Encoder.InitBitModels(this._isRep0Long);
        SevenZip.Compression.RangeCoder.Encoder.InitBitModels(this._isRep);
        SevenZip.Compression.RangeCoder.Encoder.InitBitModels(this._isRepG0);
        SevenZip.Compression.RangeCoder.Encoder.InitBitModels(this._isRepG1);
        SevenZip.Compression.RangeCoder.Encoder.InitBitModels(this._isRepG2);
        SevenZip.Compression.RangeCoder.Encoder.InitBitModels(this._posEncoders);
        this._literalEncoder.Init();
        for (int i = EMatchFinderTypeBT2; i < 4; i += EMatchFinderTypeBT4) {
            this._posSlotEncoder[i].Init();
        }
        this._lenEncoder.Init(EMatchFinderTypeBT4 << this._posStateBits);
        this._repMatchLenEncoder.Init(EMatchFinderTypeBT4 << this._posStateBits);
        this._posAlignEncoder.Init();
        this._longestMatchWasFound = false;
        this._optimumEndIndex = EMatchFinderTypeBT2;
        this._optimumCurrentIndex = EMatchFinderTypeBT2;
        this._additionalOffset = EMatchFinderTypeBT2;
    }

    int ReadMatchDistances() throws IOException {
        int lenRes = EMatchFinderTypeBT2;
        this._numDistancePairs = this._matchFinder.GetMatches(this._matchDistances);
        if (this._numDistancePairs > 0) {
            lenRes = this._matchDistances[this._numDistancePairs - 2];
            if (lenRes == this._numFastBytes) {
                lenRes += this._matchFinder.GetMatchLen(lenRes - 1, this._matchDistances[this._numDistancePairs - 1], 273 - lenRes);
            }
        }
        this._additionalOffset += EMatchFinderTypeBT4;
        return lenRes;
    }

    void MovePos(int num) throws IOException {
        if (num > 0) {
            this._matchFinder.Skip(num);
            this._additionalOffset += num;
        }
    }

    int GetRepLen1Price(int state, int posState) {
        return SevenZip.Compression.RangeCoder.Encoder.GetPrice0(this._isRepG0[state]) + SevenZip.Compression.RangeCoder.Encoder.GetPrice0(this._isRep0Long[(state << 4) + posState]);
    }

    int GetPureRepPrice(int repIndex, int state, int posState) {
        if (repIndex == 0) {
            return SevenZip.Compression.RangeCoder.Encoder.GetPrice0(this._isRepG0[state]) + SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isRep0Long[(state << 4) + posState]);
        }
        int price = SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isRepG0[state]);
        if (repIndex == EMatchFinderTypeBT4) {
            return price + SevenZip.Compression.RangeCoder.Encoder.GetPrice0(this._isRepG1[state]);
        }
        return (price + SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isRepG1[state])) + SevenZip.Compression.RangeCoder.Encoder.GetPrice(this._isRepG2[state], repIndex - 2);
    }

    int GetRepPrice(int repIndex, int len, int state, int posState) {
        return GetPureRepPrice(repIndex, state, posState) + this._repMatchLenEncoder.GetPrice(len - 2, posState);
    }

    int GetPosLenPrice(int pos, int len, int posState) {
        int price;
        int lenToPosState = Base.GetLenToPosState(len);
        if (pos < TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            price = this._distancesPrices[(lenToPosState * TransportMediator.FLAG_KEY_MEDIA_NEXT) + pos];
        } else {
            price = this._posSlotPrices[(lenToPosState << 6) + GetPosSlot2(pos)] + this._alignPrices[pos & 15];
        }
        return this._lenEncoder.GetPrice(len - 2, posState) + price;
    }

    int Backward(int cur) {
        this._optimumEndIndex = cur;
        int posMem = this._optimum[cur].PosPrev;
        int backMem = this._optimum[cur].BackPrev;
        do {
            if (this._optimum[cur].Prev1IsChar) {
                this._optimum[posMem].MakeAsChar();
                this._optimum[posMem].PosPrev = posMem - 1;
                if (this._optimum[cur].Prev2) {
                    this._optimum[posMem - 1].Prev1IsChar = false;
                    this._optimum[posMem - 1].PosPrev = this._optimum[cur].PosPrev2;
                    this._optimum[posMem - 1].BackPrev = this._optimum[cur].BackPrev2;
                }
            }
            int posPrev = posMem;
            int backCur = backMem;
            backMem = this._optimum[posPrev].BackPrev;
            posMem = this._optimum[posPrev].PosPrev;
            this._optimum[posPrev].BackPrev = backCur;
            this._optimum[posPrev].PosPrev = cur;
            cur = posPrev;
        } while (cur > 0);
        this.backRes = this._optimum[EMatchFinderTypeBT2].BackPrev;
        this._optimumCurrentIndex = this._optimum[EMatchFinderTypeBT2].PosPrev;
        return this._optimumCurrentIndex;
    }

    int GetOptimum(int position) throws IOException {
        if (this._optimumEndIndex != this._optimumCurrentIndex) {
            int lenRes = this._optimum[this._optimumCurrentIndex].PosPrev - this._optimumCurrentIndex;
            this.backRes = this._optimum[this._optimumCurrentIndex].BackPrev;
            this._optimumCurrentIndex = this._optimum[this._optimumCurrentIndex].PosPrev;
            return lenRes;
        }
        int lenMain;
        this._optimumEndIndex = EMatchFinderTypeBT2;
        this._optimumCurrentIndex = EMatchFinderTypeBT2;
        if (this._longestMatchWasFound) {
            lenMain = this._longestMatchLength;
            this._longestMatchWasFound = false;
        } else {
            lenMain = ReadMatchDistances();
        }
        int numDistancePairs = this._numDistancePairs;
        int numAvailableBytes = this._matchFinder.GetNumAvailableBytes() + EMatchFinderTypeBT4;
        if (numAvailableBytes < 2) {
            this.backRes = -1;
            return EMatchFinderTypeBT4;
        }
        int i;
        if (numAvailableBytes > 273) {
        }
        int repMaxIndex = EMatchFinderTypeBT2;
        for (i = EMatchFinderTypeBT2; i < 4; i += EMatchFinderTypeBT4) {
            this.reps[i] = this._repDistances[i];
            this.repLens[i] = this._matchFinder.GetMatchLen(-1, this.reps[i], Base.kMatchMaxLen);
            if (this.repLens[i] > this.repLens[repMaxIndex]) {
                repMaxIndex = i;
            }
        }
        if (this.repLens[repMaxIndex] >= this._numFastBytes) {
            this.backRes = repMaxIndex;
            lenRes = this.repLens[repMaxIndex];
            MovePos(lenRes - 1);
            return lenRes;
        }
        if (lenMain >= this._numFastBytes) {
            this.backRes = this._matchDistances[numDistancePairs - 1] + 4;
            MovePos(lenMain - 1);
            return lenMain;
        }
        int shortRepPrice;
        byte currentByte = this._matchFinder.GetIndexByte(-1);
        byte matchByte = this._matchFinder.GetIndexByte(((0 - this._repDistances[EMatchFinderTypeBT2]) - 1) - 1);
        if (lenMain < 2 && currentByte != matchByte) {
            if (this.repLens[repMaxIndex] < 2) {
                this.backRes = -1;
                return EMatchFinderTypeBT4;
            }
        }
        this._optimum[EMatchFinderTypeBT2].State = this._state;
        int posState = position & this._posStateMask;
        this._optimum[EMatchFinderTypeBT4].Price = this._literalEncoder.GetSubCoder(position, this._previousByte).GetPrice(!Base.StateIsCharState(this._state), matchByte, currentByte) + SevenZip.Compression.RangeCoder.Encoder.GetPrice0(this._isMatch[(this._state << 4) + posState]);
        this._optimum[EMatchFinderTypeBT4].MakeAsChar();
        int matchPrice = SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isMatch[(this._state << 4) + posState]);
        int repMatchPrice = matchPrice + SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isRep[this._state]);
        if (matchByte == currentByte) {
            shortRepPrice = repMatchPrice + GetRepLen1Price(this._state, posState);
            if (shortRepPrice < this._optimum[EMatchFinderTypeBT4].Price) {
                this._optimum[EMatchFinderTypeBT4].Price = shortRepPrice;
                this._optimum[EMatchFinderTypeBT4].MakeAsShortRep();
            }
        }
        int lenEnd = lenMain >= this.repLens[repMaxIndex] ? lenMain : this.repLens[repMaxIndex];
        if (lenEnd < 2) {
            this.backRes = this._optimum[EMatchFinderTypeBT4].BackPrev;
            return EMatchFinderTypeBT4;
        }
        int curAndLenPrice;
        Optimal optimum;
        int offs;
        this._optimum[EMatchFinderTypeBT4].PosPrev = EMatchFinderTypeBT2;
        this._optimum[EMatchFinderTypeBT2].Backs0 = this.reps[EMatchFinderTypeBT2];
        this._optimum[EMatchFinderTypeBT2].Backs1 = this.reps[EMatchFinderTypeBT4];
        this._optimum[EMatchFinderTypeBT2].Backs2 = this.reps[2];
        this._optimum[EMatchFinderTypeBT2].Backs3 = this.reps[3];
        int len = lenEnd;
        while (true) {
            int len2 = len - 1;
            this._optimum[len].Price = kIfinityPrice;
            if (len2 < 2) {
                break;
            }
            len = len2;
        }
        for (i = EMatchFinderTypeBT2; i < 4; i += EMatchFinderTypeBT4) {
            int repLen = this.repLens[i];
            if (repLen >= 2) {
                int price = repMatchPrice + GetPureRepPrice(i, this._state, posState);
                do {
                    curAndLenPrice = price + this._repMatchLenEncoder.GetPrice(repLen - 2, posState);
                    optimum = this._optimum[repLen];
                    int i2 = optimum.Price;
                    if (curAndLenPrice < r0) {
                        optimum.Price = curAndLenPrice;
                        optimum.PosPrev = EMatchFinderTypeBT2;
                        optimum.BackPrev = i;
                        optimum.Prev1IsChar = false;
                    }
                    repLen--;
                } while (repLen >= 2);
            }
        }
        int normalMatchPrice = matchPrice + SevenZip.Compression.RangeCoder.Encoder.GetPrice0(this._isRep[this._state]);
        len = this.repLens[EMatchFinderTypeBT2] >= 2 ? this.repLens[EMatchFinderTypeBT2] + EMatchFinderTypeBT4 : 2;
        if (len <= lenMain) {
            offs = EMatchFinderTypeBT2;
            while (true) {
                if (len <= this._matchDistances[offs]) {
                    break;
                }
                offs += 2;
            }
            while (true) {
                int distance = this._matchDistances[offs + EMatchFinderTypeBT4];
                curAndLenPrice = normalMatchPrice + GetPosLenPrice(distance, len, posState);
                optimum = this._optimum[len];
                i2 = optimum.Price;
                if (curAndLenPrice < r0) {
                    optimum.Price = curAndLenPrice;
                    optimum.PosPrev = EMatchFinderTypeBT2;
                    optimum.BackPrev = distance + 4;
                    optimum.Prev1IsChar = false;
                }
                if (len == this._matchDistances[offs]) {
                    offs += 2;
                    if (offs == numDistancePairs) {
                        break;
                    }
                }
                len += EMatchFinderTypeBT4;
            }
        }
        int cur = EMatchFinderTypeBT2;
        while (true) {
            cur += EMatchFinderTypeBT4;
            if (cur == lenEnd) {
                return Backward(cur);
            }
            int newLen = ReadMatchDistances();
            numDistancePairs = this._numDistancePairs;
            if (newLen >= this._numFastBytes) {
                this._longestMatchLength = newLen;
                this._longestMatchWasFound = true;
                return Backward(cur);
            }
            int state;
            position += EMatchFinderTypeBT4;
            int posPrev = this._optimum[cur].PosPrev;
            if (this._optimum[cur].Prev1IsChar) {
                posPrev--;
                if (this._optimum[cur].Prev2) {
                    state = this._optimum[this._optimum[cur].PosPrev2].State;
                    i2 = this._optimum[cur].BackPrev2;
                    if (r0 < 4) {
                        state = Base.StateUpdateRep(state);
                    } else {
                        state = Base.StateUpdateMatch(state);
                    }
                } else {
                    state = this._optimum[posPrev].State;
                }
                state = Base.StateUpdateChar(state);
            } else {
                state = this._optimum[posPrev].State;
            }
            if (posPrev == cur - 1) {
                if (this._optimum[cur].IsShortRep()) {
                    state = Base.StateUpdateShortRep(state);
                } else {
                    state = Base.StateUpdateChar(state);
                }
            } else {
                int pos;
                Optimal opt;
                if (this._optimum[cur].Prev1IsChar) {
                    if (this._optimum[cur].Prev2) {
                        posPrev = this._optimum[cur].PosPrev2;
                        pos = this._optimum[cur].BackPrev2;
                        state = Base.StateUpdateRep(state);
                        opt = this._optimum[posPrev];
                        if (pos < 4) {
                            this.reps[EMatchFinderTypeBT2] = pos - 4;
                            this.reps[EMatchFinderTypeBT4] = opt.Backs0;
                            this.reps[2] = opt.Backs1;
                            this.reps[3] = opt.Backs2;
                        } else if (pos == 0) {
                            this.reps[EMatchFinderTypeBT2] = opt.Backs0;
                            this.reps[EMatchFinderTypeBT4] = opt.Backs1;
                            this.reps[2] = opt.Backs2;
                            this.reps[3] = opt.Backs3;
                        } else if (pos == EMatchFinderTypeBT4) {
                            this.reps[EMatchFinderTypeBT2] = opt.Backs1;
                            this.reps[EMatchFinderTypeBT4] = opt.Backs0;
                            this.reps[2] = opt.Backs2;
                            this.reps[3] = opt.Backs3;
                        } else if (pos != 2) {
                            this.reps[EMatchFinderTypeBT2] = opt.Backs2;
                            this.reps[EMatchFinderTypeBT4] = opt.Backs0;
                            this.reps[2] = opt.Backs1;
                            this.reps[3] = opt.Backs3;
                        } else {
                            this.reps[EMatchFinderTypeBT2] = opt.Backs3;
                            this.reps[EMatchFinderTypeBT4] = opt.Backs0;
                            this.reps[2] = opt.Backs1;
                            this.reps[3] = opt.Backs2;
                        }
                    }
                }
                pos = this._optimum[cur].BackPrev;
                if (pos < 4) {
                    state = Base.StateUpdateRep(state);
                } else {
                    state = Base.StateUpdateMatch(state);
                }
                opt = this._optimum[posPrev];
                if (pos < 4) {
                    this.reps[EMatchFinderTypeBT2] = pos - 4;
                    this.reps[EMatchFinderTypeBT4] = opt.Backs0;
                    this.reps[2] = opt.Backs1;
                    this.reps[3] = opt.Backs2;
                } else if (pos == 0) {
                    this.reps[EMatchFinderTypeBT2] = opt.Backs0;
                    this.reps[EMatchFinderTypeBT4] = opt.Backs1;
                    this.reps[2] = opt.Backs2;
                    this.reps[3] = opt.Backs3;
                } else if (pos == EMatchFinderTypeBT4) {
                    this.reps[EMatchFinderTypeBT2] = opt.Backs1;
                    this.reps[EMatchFinderTypeBT4] = opt.Backs0;
                    this.reps[2] = opt.Backs2;
                    this.reps[3] = opt.Backs3;
                } else if (pos != 2) {
                    this.reps[EMatchFinderTypeBT2] = opt.Backs3;
                    this.reps[EMatchFinderTypeBT4] = opt.Backs0;
                    this.reps[2] = opt.Backs1;
                    this.reps[3] = opt.Backs2;
                } else {
                    this.reps[EMatchFinderTypeBT2] = opt.Backs2;
                    this.reps[EMatchFinderTypeBT4] = opt.Backs0;
                    this.reps[2] = opt.Backs1;
                    this.reps[3] = opt.Backs3;
                }
            }
            this._optimum[cur].State = state;
            this._optimum[cur].Backs0 = this.reps[EMatchFinderTypeBT2];
            this._optimum[cur].Backs1 = this.reps[EMatchFinderTypeBT4];
            this._optimum[cur].Backs2 = this.reps[2];
            this._optimum[cur].Backs3 = this.reps[3];
            int curPrice = this._optimum[cur].Price;
            currentByte = this._matchFinder.GetIndexByte(-1);
            matchByte = this._matchFinder.GetIndexByte(((0 - this.reps[EMatchFinderTypeBT2]) - 1) - 1);
            posState = position & this._posStateMask;
            int curAnd1Price = (curPrice + SevenZip.Compression.RangeCoder.Encoder.GetPrice0(this._isMatch[(state << 4) + posState])) + this._literalEncoder.GetSubCoder(position, this._matchFinder.GetIndexByte(-2)).GetPrice(!Base.StateIsCharState(state), matchByte, currentByte);
            Optimal nextOptimum = this._optimum[cur + EMatchFinderTypeBT4];
            boolean nextIsChar = false;
            i2 = nextOptimum.Price;
            if (curAnd1Price < r0) {
                nextOptimum.Price = curAnd1Price;
                nextOptimum.PosPrev = cur;
                nextOptimum.MakeAsChar();
                nextIsChar = true;
            }
            matchPrice = curPrice + SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isMatch[(state << 4) + posState]);
            repMatchPrice = matchPrice + SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isRep[state]);
            if (matchByte == currentByte) {
                i2 = nextOptimum.PosPrev;
                if (r0 >= cur || nextOptimum.BackPrev != 0) {
                    shortRepPrice = repMatchPrice + GetRepLen1Price(state, posState);
                    if (shortRepPrice <= nextOptimum.Price) {
                        nextOptimum.Price = shortRepPrice;
                        nextOptimum.PosPrev = cur;
                        nextOptimum.MakeAsShortRep();
                        nextIsChar = true;
                    }
                }
            }
            int numAvailableBytesFull = Math.min(4095 - cur, this._matchFinder.GetNumAvailableBytes() + EMatchFinderTypeBT4);
            numAvailableBytes = numAvailableBytesFull;
            if (numAvailableBytes >= 2) {
                int lenTest2;
                int state2;
                int posStateNext;
                int nextRepMatchPrice;
                int offset;
                int lenTest;
                int curAndLenCharPrice;
                if (numAvailableBytes > this._numFastBytes) {
                    numAvailableBytes = this._numFastBytes;
                }
                if (!(nextIsChar || matchByte == currentByte)) {
                    lenTest2 = this._matchFinder.GetMatchLen(EMatchFinderTypeBT2, this.reps[EMatchFinderTypeBT2], Math.min(numAvailableBytesFull - 1, this._numFastBytes));
                    if (lenTest2 >= 2) {
                        state2 = Base.StateUpdateChar(state);
                        posStateNext = (position + EMatchFinderTypeBT4) & this._posStateMask;
                        nextRepMatchPrice = (SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isMatch[(state2 << 4) + posStateNext]) + curAnd1Price) + SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isRep[state2]);
                        offset = (cur + EMatchFinderTypeBT4) + lenTest2;
                        while (lenEnd < offset) {
                            lenEnd += EMatchFinderTypeBT4;
                            this._optimum[lenEnd].Price = kIfinityPrice;
                        }
                        curAndLenPrice = nextRepMatchPrice + GetRepPrice(EMatchFinderTypeBT2, lenTest2, state2, posStateNext);
                        optimum = this._optimum[offset];
                        i2 = optimum.Price;
                        if (curAndLenPrice < r0) {
                            optimum.Price = curAndLenPrice;
                            optimum.PosPrev = cur + EMatchFinderTypeBT4;
                            optimum.BackPrev = EMatchFinderTypeBT2;
                            optimum.Prev1IsChar = true;
                            optimum.Prev2 = false;
                        }
                    }
                }
                int startLen = 2;
                for (int repIndex = EMatchFinderTypeBT2; repIndex < 4; repIndex += EMatchFinderTypeBT4) {
                    lenTest = this._matchFinder.GetMatchLen(-1, this.reps[repIndex], numAvailableBytes);
                    if (lenTest >= 2) {
                        int lenTestTemp = lenTest;
                        while (true) {
                            if (lenEnd < cur + lenTest) {
                                lenEnd += EMatchFinderTypeBT4;
                                this._optimum[lenEnd].Price = kIfinityPrice;
                            } else {
                                curAndLenPrice = repMatchPrice + GetRepPrice(repIndex, lenTest, state, posState);
                                optimum = this._optimum[cur + lenTest];
                                i2 = optimum.Price;
                                if (curAndLenPrice < r0) {
                                    optimum.Price = curAndLenPrice;
                                    optimum.PosPrev = cur;
                                    optimum.BackPrev = repIndex;
                                    optimum.Prev1IsChar = false;
                                }
                                lenTest--;
                                if (lenTest < 2) {
                                    break;
                                }
                            }
                        }
                        lenTest = lenTestTemp;
                        if (repIndex == 0) {
                            startLen = lenTest + EMatchFinderTypeBT4;
                        }
                        if (lenTest < numAvailableBytesFull) {
                            lenTest2 = this._matchFinder.GetMatchLen(lenTest, this.reps[repIndex], Math.min((numAvailableBytesFull - 1) - lenTest, this._numFastBytes));
                            if (lenTest2 >= 2) {
                                state2 = Base.StateUpdateRep(state);
                                curAndLenCharPrice = ((GetRepPrice(repIndex, lenTest, state, posState) + repMatchPrice) + SevenZip.Compression.RangeCoder.Encoder.GetPrice0(this._isMatch[(state2 << 4) + ((position + lenTest) & this._posStateMask)])) + this._literalEncoder.GetSubCoder(position + lenTest, this._matchFinder.GetIndexByte((lenTest - 1) - 1)).GetPrice(true, this._matchFinder.GetIndexByte((lenTest - 1) - (this.reps[repIndex] + EMatchFinderTypeBT4)), this._matchFinder.GetIndexByte(lenTest - 1));
                                state2 = Base.StateUpdateChar(state2);
                                posStateNext = ((position + lenTest) + EMatchFinderTypeBT4) & this._posStateMask;
                                nextRepMatchPrice = (curAndLenCharPrice + SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isMatch[(state2 << 4) + posStateNext])) + SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isRep[state2]);
                                offset = (lenTest + EMatchFinderTypeBT4) + lenTest2;
                                while (lenEnd < cur + offset) {
                                    lenEnd += EMatchFinderTypeBT4;
                                    this._optimum[lenEnd].Price = kIfinityPrice;
                                }
                                curAndLenPrice = nextRepMatchPrice + GetRepPrice(EMatchFinderTypeBT2, lenTest2, state2, posStateNext);
                                optimum = this._optimum[cur + offset];
                                i2 = optimum.Price;
                                if (curAndLenPrice < r0) {
                                    optimum.Price = curAndLenPrice;
                                    optimum.PosPrev = (cur + lenTest) + EMatchFinderTypeBT4;
                                    optimum.BackPrev = EMatchFinderTypeBT2;
                                    optimum.Prev1IsChar = true;
                                    optimum.Prev2 = true;
                                    optimum.PosPrev2 = cur;
                                    optimum.BackPrev2 = repIndex;
                                }
                            }
                        }
                    }
                }
                if (newLen > numAvailableBytes) {
                    newLen = numAvailableBytes;
                    numDistancePairs = EMatchFinderTypeBT2;
                    while (true) {
                        if (newLen <= this._matchDistances[numDistancePairs]) {
                            break;
                        }
                        numDistancePairs += 2;
                    }
                    this._matchDistances[numDistancePairs] = newLen;
                    numDistancePairs += 2;
                }
                if (newLen >= startLen) {
                    normalMatchPrice = matchPrice + SevenZip.Compression.RangeCoder.Encoder.GetPrice0(this._isRep[state]);
                    while (lenEnd < cur + newLen) {
                        lenEnd += EMatchFinderTypeBT4;
                        this._optimum[lenEnd].Price = kIfinityPrice;
                    }
                    offs = EMatchFinderTypeBT2;
                    while (true) {
                        if (startLen <= this._matchDistances[offs]) {
                            break;
                        }
                        offs += 2;
                    }
                    lenTest = startLen;
                    while (true) {
                        int curBack = this._matchDistances[offs + EMatchFinderTypeBT4];
                        curAndLenPrice = normalMatchPrice + GetPosLenPrice(curBack, lenTest, posState);
                        optimum = this._optimum[cur + lenTest];
                        i2 = optimum.Price;
                        if (curAndLenPrice < r0) {
                            optimum.Price = curAndLenPrice;
                            optimum.PosPrev = cur;
                            optimum.BackPrev = curBack + 4;
                            optimum.Prev1IsChar = false;
                        }
                        if (lenTest == this._matchDistances[offs]) {
                            if (lenTest < numAvailableBytesFull) {
                                lenTest2 = this._matchFinder.GetMatchLen(lenTest, curBack, Math.min((numAvailableBytesFull - 1) - lenTest, this._numFastBytes));
                                if (lenTest2 >= 2) {
                                    state2 = Base.StateUpdateMatch(state);
                                    curAndLenCharPrice = (SevenZip.Compression.RangeCoder.Encoder.GetPrice0(this._isMatch[(state2 << 4) + ((position + lenTest) & this._posStateMask)]) + curAndLenPrice) + this._literalEncoder.GetSubCoder(position + lenTest, this._matchFinder.GetIndexByte((lenTest - 1) - 1)).GetPrice(true, this._matchFinder.GetIndexByte((lenTest - (curBack + EMatchFinderTypeBT4)) - 1), this._matchFinder.GetIndexByte(lenTest - 1));
                                    state2 = Base.StateUpdateChar(state2);
                                    posStateNext = ((position + lenTest) + EMatchFinderTypeBT4) & this._posStateMask;
                                    nextRepMatchPrice = (curAndLenCharPrice + SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isMatch[(state2 << 4) + posStateNext])) + SevenZip.Compression.RangeCoder.Encoder.GetPrice1(this._isRep[state2]);
                                    offset = (lenTest + EMatchFinderTypeBT4) + lenTest2;
                                    while (lenEnd < cur + offset) {
                                        lenEnd += EMatchFinderTypeBT4;
                                        this._optimum[lenEnd].Price = kIfinityPrice;
                                    }
                                    curAndLenPrice = nextRepMatchPrice + GetRepPrice(EMatchFinderTypeBT2, lenTest2, state2, posStateNext);
                                    optimum = this._optimum[cur + offset];
                                    i2 = optimum.Price;
                                    if (curAndLenPrice < r0) {
                                        optimum.Price = curAndLenPrice;
                                        optimum.PosPrev = (cur + lenTest) + EMatchFinderTypeBT4;
                                        optimum.BackPrev = EMatchFinderTypeBT2;
                                        optimum.Prev1IsChar = true;
                                        optimum.Prev2 = true;
                                        optimum.PosPrev2 = cur;
                                        optimum.BackPrev2 = curBack + 4;
                                    }
                                }
                            }
                            offs += 2;
                            if (offs == numDistancePairs) {
                                break;
                            }
                        }
                        lenTest += EMatchFinderTypeBT4;
                    }
                }
            }
        }
    }

    boolean ChangePair(int smallDist, int bigDist) {
        return smallDist < PageTransition.FROM_ADDRESS_BAR && bigDist >= (smallDist << 7);
    }

    void WriteEndMarker(int posState) throws IOException {
        if (this._writeEndMark) {
            this._rangeEncoder.Encode(this._isMatch, (this._state << 4) + posState, EMatchFinderTypeBT4);
            this._rangeEncoder.Encode(this._isRep, this._state, EMatchFinderTypeBT2);
            this._state = Base.StateUpdateMatch(this._state);
            this._lenEncoder.Encode(this._rangeEncoder, EMatchFinderTypeBT2, posState);
            this._posSlotEncoder[Base.GetLenToPosState(2)].Encode(this._rangeEncoder, 63);
            int posReduced = PageTransition.CLIENT_REDIRECT - 1;
            this._rangeEncoder.EncodeDirectBits(67108863, 26);
            this._posAlignEncoder.ReverseEncode(this._rangeEncoder, 15);
        }
    }

    void Flush(int nowPos) throws IOException {
        ReleaseMFStream();
        WriteEndMarker(this._posStateMask & nowPos);
        this._rangeEncoder.FlushData();
        this._rangeEncoder.FlushStream();
    }

    public void CodeOneBlock(long[] inSize, long[] outSize, boolean[] finished) throws IOException {
        inSize[EMatchFinderTypeBT2] = 0;
        outSize[EMatchFinderTypeBT2] = 0;
        finished[EMatchFinderTypeBT2] = true;
        if (this._inStream != null) {
            this._matchFinder.SetStream(this._inStream);
            this._matchFinder.Init();
            this._needReleaseMFStream = true;
            this._inStream = null;
        }
        if (!this._finished) {
            int posState;
            byte curByte;
            this._finished = true;
            long progressPosValuePrev = this.nowPos64;
            if (this.nowPos64 == 0) {
                if (this._matchFinder.GetNumAvailableBytes() == 0) {
                    Flush((int) this.nowPos64);
                    return;
                }
                ReadMatchDistances();
                posState = ((int) this.nowPos64) & this._posStateMask;
                this._rangeEncoder.Encode(this._isMatch, (this._state << 4) + posState, EMatchFinderTypeBT2);
                this._state = Base.StateUpdateChar(this._state);
                curByte = this._matchFinder.GetIndexByte(0 - this._additionalOffset);
                this._literalEncoder.GetSubCoder((int) this.nowPos64, this._previousByte).Encode(this._rangeEncoder, curByte);
                this._previousByte = curByte;
                this._additionalOffset--;
                this.nowPos64++;
            }
            if (this._matchFinder.GetNumAvailableBytes() == 0) {
                Flush((int) this.nowPos64);
                return;
            }
            while (true) {
                int len = GetOptimum((int) this.nowPos64);
                int pos = this.backRes;
                posState = ((int) this.nowPos64) & this._posStateMask;
                int complexState = (this._state << 4) + posState;
                if (len == EMatchFinderTypeBT4 && pos == -1) {
                    this._rangeEncoder.Encode(this._isMatch, complexState, EMatchFinderTypeBT2);
                    curByte = this._matchFinder.GetIndexByte(0 - this._additionalOffset);
                    Encoder2 subCoder = this._literalEncoder.GetSubCoder((int) this.nowPos64, this._previousByte);
                    if (Base.StateIsCharState(this._state)) {
                        subCoder.Encode(this._rangeEncoder, curByte);
                    } else {
                        BinTree binTree = this._matchFinder;
                        int[] iArr = this._repDistances;
                        byte matchByte = r0.GetIndexByte(((0 - r0[EMatchFinderTypeBT2]) - 1) - this._additionalOffset);
                        subCoder.EncodeMatched(this._rangeEncoder, matchByte, curByte);
                    }
                    this._previousByte = curByte;
                    this._state = Base.StateUpdateChar(this._state);
                } else {
                    this._rangeEncoder.Encode(this._isMatch, complexState, EMatchFinderTypeBT4);
                    int distance;
                    int i;
                    if (pos < 4) {
                        this._rangeEncoder.Encode(this._isRep, this._state, EMatchFinderTypeBT4);
                        if (pos == 0) {
                            this._rangeEncoder.Encode(this._isRepG0, this._state, EMatchFinderTypeBT2);
                            if (len == EMatchFinderTypeBT4) {
                                this._rangeEncoder.Encode(this._isRep0Long, complexState, EMatchFinderTypeBT2);
                            } else {
                                this._rangeEncoder.Encode(this._isRep0Long, complexState, EMatchFinderTypeBT4);
                            }
                        } else {
                            this._rangeEncoder.Encode(this._isRepG0, this._state, EMatchFinderTypeBT4);
                            if (pos == EMatchFinderTypeBT4) {
                                this._rangeEncoder.Encode(this._isRepG1, this._state, EMatchFinderTypeBT2);
                            } else {
                                this._rangeEncoder.Encode(this._isRepG1, this._state, EMatchFinderTypeBT4);
                                this._rangeEncoder.Encode(this._isRepG2, this._state, pos - 2);
                            }
                        }
                        if (len == EMatchFinderTypeBT4) {
                            this._state = Base.StateUpdateShortRep(this._state);
                        } else {
                            this._repMatchLenEncoder.Encode(this._rangeEncoder, len - 2, posState);
                            this._state = Base.StateUpdateRep(this._state);
                        }
                        distance = this._repDistances[pos];
                        if (pos != 0) {
                            for (i = pos; i >= EMatchFinderTypeBT4; i--) {
                                this._repDistances[i] = this._repDistances[i - 1];
                            }
                            this._repDistances[EMatchFinderTypeBT2] = distance;
                        }
                    } else {
                        this._rangeEncoder.Encode(this._isRep, this._state, EMatchFinderTypeBT2);
                        this._state = Base.StateUpdateMatch(this._state);
                        this._lenEncoder.Encode(this._rangeEncoder, len - 2, posState);
                        pos -= 4;
                        int posSlot = GetPosSlot(pos);
                        int lenToPosState = Base.GetLenToPosState(len);
                        this._posSlotEncoder[lenToPosState].Encode(this._rangeEncoder, posSlot);
                        if (posSlot >= 4) {
                            int footerBits = (posSlot >> EMatchFinderTypeBT4) - 1;
                            int baseVal = ((posSlot & EMatchFinderTypeBT4) | 2) << footerBits;
                            int posReduced = pos - baseVal;
                            if (posSlot < 14) {
                                BitTreeEncoder.ReverseEncode(this._posEncoders, (baseVal - posSlot) - 1, this._rangeEncoder, footerBits, posReduced);
                            } else {
                                this._rangeEncoder.EncodeDirectBits(posReduced >> 4, footerBits - 4);
                                this._posAlignEncoder.ReverseEncode(this._rangeEncoder, posReduced & 15);
                                this._alignPriceCount += EMatchFinderTypeBT4;
                            }
                        }
                        distance = pos;
                        for (i = 3; i >= EMatchFinderTypeBT4; i--) {
                            this._repDistances[i] = this._repDistances[i - 1];
                        }
                        this._repDistances[EMatchFinderTypeBT2] = distance;
                        this._matchPriceCount += EMatchFinderTypeBT4;
                    }
                    this._previousByte = this._matchFinder.GetIndexByte((len - 1) - this._additionalOffset);
                }
                this._additionalOffset -= len;
                this.nowPos64 += (long) len;
                if (this._additionalOffset == 0) {
                    int i2 = this._matchPriceCount;
                    if (r0 >= 128) {
                        FillDistancesPrices();
                    }
                    i2 = this._alignPriceCount;
                    if (r0 >= kNumLenSpecSymbols) {
                        FillAlignPrices();
                    }
                    inSize[EMatchFinderTypeBT2] = this.nowPos64;
                    outSize[EMatchFinderTypeBT2] = this._rangeEncoder.GetProcessedSizeAdd();
                    if (this._matchFinder.GetNumAvailableBytes() == 0) {
                        Flush((int) this.nowPos64);
                        return;
                    }
                    if (this.nowPos64 - progressPosValuePrev >= PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM) {
                        this._finished = false;
                        finished[EMatchFinderTypeBT2] = false;
                        return;
                    }
                }
            }
        }
    }

    void ReleaseMFStream() {
        if (this._matchFinder != null && this._needReleaseMFStream) {
            this._matchFinder.ReleaseStream();
            this._needReleaseMFStream = false;
        }
    }

    void SetOutStream(OutputStream outStream) {
        this._rangeEncoder.SetStream(outStream);
    }

    void ReleaseOutStream() {
        this._rangeEncoder.ReleaseStream();
    }

    void ReleaseStreams() {
        ReleaseMFStream();
        ReleaseOutStream();
    }

    void SetStreams(InputStream inStream, OutputStream outStream, long inSize, long outSize) {
        this._inStream = inStream;
        this._finished = false;
        Create();
        SetOutStream(outStream);
        Init();
        FillDistancesPrices();
        FillAlignPrices();
        this._lenEncoder.SetTableSize((this._numFastBytes + EMatchFinderTypeBT4) - 2);
        this._lenEncoder.UpdateTables(EMatchFinderTypeBT4 << this._posStateBits);
        this._repMatchLenEncoder.SetTableSize((this._numFastBytes + EMatchFinderTypeBT4) - 2);
        this._repMatchLenEncoder.UpdateTables(EMatchFinderTypeBT4 << this._posStateBits);
        this.nowPos64 = 0;
    }

    public void Code(InputStream inStream, OutputStream outStream, long inSize, long outSize, ICodeProgress progress) throws IOException {
        this._needReleaseMFStream = false;
        try {
            SetStreams(inStream, outStream, inSize, outSize);
            while (true) {
                CodeOneBlock(this.processedInSize, this.processedOutSize, this.finished);
                if (this.finished[EMatchFinderTypeBT2]) {
                    break;
                } else if (progress != null) {
                    progress.SetProgress(this.processedInSize[EMatchFinderTypeBT2], this.processedOutSize[EMatchFinderTypeBT2]);
                }
            }
        } finally {
            ReleaseStreams();
        }
    }

    public void WriteCoderProperties(OutputStream outStream) throws IOException {
        this.properties[EMatchFinderTypeBT2] = (byte) ((((this._posStateBits * kPropSize) + this._numLiteralPosStateBits) * 9) + this._numLiteralContextBits);
        for (int i = EMatchFinderTypeBT2; i < 4; i += EMatchFinderTypeBT4) {
            this.properties[i + EMatchFinderTypeBT4] = (byte) (this._dictionarySize >> (i * 8));
        }
        outStream.write(this.properties, EMatchFinderTypeBT2, kPropSize);
    }

    void FillDistancesPrices() {
        int i;
        for (i = 4; i < TransportMediator.FLAG_KEY_MEDIA_NEXT; i += EMatchFinderTypeBT4) {
            int posSlot = GetPosSlot(i);
            int footerBits = (posSlot >> EMatchFinderTypeBT4) - 1;
            int baseVal = ((posSlot & EMatchFinderTypeBT4) | 2) << footerBits;
            this.tempPrices[i] = BitTreeEncoder.ReverseGetPrice(this._posEncoders, (baseVal - posSlot) - 1, footerBits, i - baseVal);
        }
        for (int lenToPosState = EMatchFinderTypeBT2; lenToPosState < 4; lenToPosState += EMatchFinderTypeBT4) {
            BitTreeEncoder encoder = this._posSlotEncoder[lenToPosState];
            int st = lenToPosState << 6;
            for (posSlot = EMatchFinderTypeBT2; posSlot < this._distTableSize; posSlot += EMatchFinderTypeBT4) {
                this._posSlotPrices[st + posSlot] = encoder.GetPrice(posSlot);
            }
            for (posSlot = 14; posSlot < this._distTableSize; posSlot += EMatchFinderTypeBT4) {
                int[] iArr = this._posSlotPrices;
                int i2 = st + posSlot;
                iArr[i2] = iArr[i2] + ((((posSlot >> EMatchFinderTypeBT4) - 1) - 4) << 6);
            }
            int st2 = lenToPosState * TransportMediator.FLAG_KEY_MEDIA_NEXT;
            i = EMatchFinderTypeBT2;
            while (i < 4) {
                this._distancesPrices[st2 + i] = this._posSlotPrices[st + i];
                i += EMatchFinderTypeBT4;
            }
            while (i < TransportMediator.FLAG_KEY_MEDIA_NEXT) {
                this._distancesPrices[st2 + i] = this._posSlotPrices[GetPosSlot(i) + st] + this.tempPrices[i];
                i += EMatchFinderTypeBT4;
            }
        }
        this._matchPriceCount = EMatchFinderTypeBT2;
    }

    void FillAlignPrices() {
        for (int i = EMatchFinderTypeBT2; i < kNumLenSpecSymbols; i += EMatchFinderTypeBT4) {
            this._alignPrices[i] = this._posAlignEncoder.ReverseGetPrice(i);
        }
        this._alignPriceCount = EMatchFinderTypeBT2;
    }

    public boolean SetAlgorithm(int algorithm) {
        return true;
    }

    public boolean SetDictionarySize(int dictionarySize) {
        if (dictionarySize < EMatchFinderTypeBT4 || dictionarySize > PageTransition.CHAIN_END) {
            return false;
        }
        this._dictionarySize = dictionarySize;
        int dicLogSize = EMatchFinderTypeBT2;
        while (dictionarySize > (EMatchFinderTypeBT4 << dicLogSize)) {
            dicLogSize += EMatchFinderTypeBT4;
        }
        this._distTableSize = dicLogSize * 2;
        return true;
    }

    public boolean SetNumFastBytes(int numFastBytes) {
        if (numFastBytes < kPropSize || numFastBytes > Base.kMatchMaxLen) {
            return false;
        }
        this._numFastBytes = numFastBytes;
        return true;
    }

    public boolean SetMatchFinder(int matchFinderIndex) {
        if (matchFinderIndex < 0 || matchFinderIndex > 2) {
            return false;
        }
        int matchFinderIndexPrev = this._matchFinderType;
        this._matchFinderType = matchFinderIndex;
        if (!(this._matchFinder == null || matchFinderIndexPrev == this._matchFinderType)) {
            this._dictionarySizePrev = -1;
            this._matchFinder = null;
        }
        return true;
    }

    public boolean SetLcLpPb(int lc, int lp, int pb) {
        if (lp < 0 || lp > 4 || lc < 0 || lc > 8 || pb < 0 || pb > 4) {
            return false;
        }
        this._numLiteralPosStateBits = lp;
        this._numLiteralContextBits = lc;
        this._posStateBits = pb;
        this._posStateMask = (EMatchFinderTypeBT4 << this._posStateBits) - 1;
        return true;
    }

    public void SetEndMarkerMode(boolean endMarkerMode) {
        this._writeEndMark = endMarkerMode;
    }
}
