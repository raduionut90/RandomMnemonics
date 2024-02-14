package org.example.utils;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

public class PKCS5S2ParametersGenerator {
    private Mac hMac;
    private byte[] state;
    protected byte[]  password;
    protected byte[]  salt;
    protected int     iterationCount;
    /**
     * construct a PKCS5 Scheme 2 Parameters generator.
     */

    public PKCS5S2ParametersGenerator(Digest digest,
                                      byte[]  password,
                                      byte[]  salt,
                                      int     iterationCount)
    {
        hMac = new HMac(digest);
        state = new byte[hMac.getMacSize()];
        this.password = password;
        this.salt = salt;
        this.iterationCount = iterationCount;
    }

    public byte[] generateDerivedKey(
            int dkLen)
    {
        int     hLen = 64;
        int     l = 1;
        byte[]  iBuf = new byte[4];
        byte[]  outBytes = new byte[l * hLen];
        int     outPos = 0;

        CipherParameters param = new KeyParameter(password);

        hMac.init(param);

        for (int i = 1; i <= l; i++)
        {
            // Increment the value in 'iBuf'
            int pos = 3;
            while (++iBuf[pos] == 0)
            {
                --pos;
            }

            F(salt, iterationCount, iBuf, outBytes, outPos);
            outPos += hLen;
        }

        return outBytes;
    }

    private void F(
            byte[]  S,
            int     c,
            byte[]  iBuf,
            byte[]  out,
            int     outOff)
    {
        if (c == 0)
        {
            throw new IllegalArgumentException("iteration count must be at least 1.");
        }

        if (S != null)
        {
            hMac.update(S, 0, S.length);
        }

        hMac.update(iBuf, 0, iBuf.length);
        hMac.doFinal(state, 0);

        System.arraycopy(state, 0, out, outOff, state.length);

        for (int count = 1; count < c; count++)
        {
            hMac.update(state, 0, state.length);
            hMac.doFinal(state, 0);

            for (int j = 0; j != state.length; j++)
            {
                out[outOff + j] ^= state[j];
            }
        }
    }
}
