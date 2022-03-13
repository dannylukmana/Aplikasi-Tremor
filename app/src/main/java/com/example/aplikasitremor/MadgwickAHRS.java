package com.example.aplikasitremor;

public class MadgwickAHRS {
    private float samplePeriod;
    private float beta;
    private float[] quaternion;


    public float getSamplePeriod() {
        return samplePeriod;
    }

    public void setSamplePeriod(float samplePeriod) {
        this.samplePeriod = samplePeriod;
    }

    public float getBeta() {
        return beta;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public float[] getQuaternion() {
        return quaternion;
    }

    /**
     * Initializes a new instance of the {@link MadgwickAHRS} class.
     *
     * @param samplePeriod
     *            Sample period.
     * @param beta
     *            Algorithm gain beta.
     */
    public MadgwickAHRS(float samplePeriod, float beta) {
        this.samplePeriod = samplePeriod;
        this.beta = beta;
        this.quaternion = new float[] { 1f, 0f, 0f, 0f };
    }



    public void update(double Gx, double Gy, double Gz, float Ax, float Ay,float Az) {
        float q1 = quaternion[0], q2 = quaternion[1], q3 = quaternion[2], q4 = quaternion[3]; // short
        // name
        // local
        // variable
        // for
        // readability
        float norm;
        float s1, s2, s3, s4;
        double qDot1, qDot2, qDot3, qDot4;

        // Auxiliary variables to avoid repeated arithmetic
        float _2q1 = 2f * q1;
        float _2q2 = 2f * q2;
        float _2q3 = 2f * q3;
        float _2q4 = 2f * q4;
        float _4q1 = 4f * q1;
        float _4q2 = 4f * q2;
        float _4q3 = 4f * q3;
        float _8q2 = 8f * q2;
        float _8q3 = 8f * q3;
        float q1q1 = q1 * q1;
        float q2q2 = q2 * q2;
        float q3q3 = q3 * q3;
        float q4q4 = q4 * q4;

        // Normalise accelerometer measurement
        norm = (float) Math.sqrt(Ax * Ax + Ay * Ay + Az * Az);
        if (norm == 0f)
            return; // handle NaN
        norm = 1 / norm; // use reciprocal for division
        Ax *= norm;
        Ay *= norm;
        Az *= norm;

        // Gradient decent algorithm corrective step
        s1 = _4q1 * q3q3 + _2q3 * Ax + _4q1 * q2q2 - _2q2 * Ay;
        s2 = _4q2 * q4q4 - _2q4 * Ax + 4f * q1q1 * q2 - _2q1 * Ay - _4q2 + _8q2
                * q2q2 + _8q2 * q3q3 + _4q2 * Az;
        s3 = 4f * q1q1 * q3 + _2q1 * Ax + _4q3 * q4q4 - _2q4 * Ay - _4q3 + _8q3
                * q2q2 + _8q3 * q3q3 + _4q3 * Az;
        s4 = 4f * q2q2 * q4 - _2q2 * Ax + 4f * q3q3 * q4 - _2q3 * Ay;
        norm = 1f / (float) Math.sqrt(s1 * s1 + s2 * s2 + s3 * s3 + s4 * s4); // normalise
        // step
        // magnitude
        s1 *= norm;
        s2 *= norm;
        s3 *= norm;
        s4 *= norm;

        // Compute rate of change of quaternion
        qDot1 = 0.5f * (-q2 * Gx - q3 * Gy - q4 * Gz) - beta * s1;
        qDot2 = 0.5f * (q1 * Gx + q3 * Gz - q4 * Gy) - beta * s2;
        qDot3 = 0.5f * (q1 * Gy - q2 * Gz + q4 * Gx) - beta * s3;
        qDot4 = 0.5f * (q1 * Gz + q2 * Gy - q3 * Gx) - beta * s4;

        // Integrate to yield quaternion
        q1 += qDot1 * samplePeriod;
        q2 += qDot2 * samplePeriod;
        q3 += qDot3 * samplePeriod;
        q4 += qDot4 * samplePeriod;
        norm = 1f / (float) Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4); // normalise
        // quaternion
        quaternion[0] = q1 * norm;
        quaternion[1] = q2 * norm;
        quaternion[2] = q3 * norm;
        quaternion[3] = q4 * norm;
    }




}

